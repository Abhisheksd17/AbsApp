package com.example.data.repositoryImpl

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.common.datastore.DataStore
import com.example.data.mapper.ChatMapper
import com.example.data.wrapper.BaseApiResponse
import com.example.data.wrapper.CloudinaryService
import com.example.data.wrapper.WebSocketManager
import com.example.model.websocket.WsEvent
import com.example.database.dao.MessageDao
import com.example.database.dao.UserDao
import com.example.database.entity.MessageEntity
import com.example.database.entity.UserEntity
import com.example.domain.data.NetworkResult
import com.example.domain.repository.MessageRepository
import com.example.model.media.MediaUploadRequest
import com.example.model.message.MessageStatus
import com.example.model.message.MessageWithUser
import com.example.model.message.SendMessageRequest
import com.example.model.message.UserUi
import com.example.model.websocket.WsNewMessage
import com.example.model.websocket.WsReceipt
import com.example.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val messageDao: MessageDao,
    private val mapper: ChatMapper,
    private val userDao: UserDao,
    private val dataStore: DataStore,
    private val wsManager: WebSocketManager,
    private val cloudinaryService: CloudinaryService,
    @ApplicationContext private val context: Context
) : MessageRepository, BaseApiResponse() {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        consumeWsEvents()
    }

    private val _isTyping =
        MutableStateFlow(false)

    override val isTyping =
        _isTyping.asStateFlow()

    var nextCursor: String? = null
    val limit = 20



    private fun consumeWsEvents() {
        repoScope.launch {
            wsManager.events.collect { event ->
                when (event) {
                    is WsEvent.NewMessage   -> handleIncomingMessage(event.payload)
                    is WsEvent.Receipt      -> handleReceipt(event.payload)
                    is WsEvent.Typing -> {
                        _isTyping.value = event.isTyping
                    }
                    is WsEvent.Connected    -> Log.d("MsgRepo", "WS connected")
                    is WsEvent.Disconnected -> Log.d("MsgRepo", "WS disconnected")
                    else                    -> Unit
                }
            }
        }
    }



    // ─────────────────────────────────────────────
    // WebSocket event handlers
    // ─────────────────────────────────────────────

    /**
     * A new message arrived over the socket.
     *
     * Strategy:
     *  - If we already have a row with this serverId (= we sent it and
     *    mapClientToServer already ran), just update the status.
     *  - Otherwise insert as a fresh incoming message.
     *
     * This prevents the duplicate-row bug where the local optimistic row
     * and the socket-delivered row coexist because the socket payload
     * carries no clientId.
     */
    private suspend fun handleIncomingMessage(payload: WsNewMessage) {
        val entity = MessageEntity(
            localId = 0,              // Room auto-generates; ignored on upsert
            serverId = payload.msgId,
            clientId = null,           // socket never carries clientId
            chatId = payload.chatId,
            senderId = payload.senderId,
            type = payload.type,
            body = payload.body,
            mediaId = payload.mediaId,
            mediaUrl = payload.mediaUrl,
            mediaThumb = payload.mediaThumb,
            replyToId = payload.replyToId,
            createdAt = payload.createdAt,
            isForwarded = payload.isForwarded,
            readByMe = false,
            status = MessageStatus.SENT,
        )
        upsertIncomingMessage(entity)

        wsManager.sendAck(payload.msgId, "delivered")
    }

    /**
     * A delivery/read receipt arrived over the socket.
     * We always identify the row by serverId here — clientId is irrelevant
     * because the receipt only concerns a message already confirmed by the server.
     */
    private suspend fun handleReceipt(payload: WsReceipt) {
        val newStatus = when (payload.status) {
            "delivered" -> MessageStatus.DELIVERED
            "read"      -> MessageStatus.READ
            else        -> return
        }
        messageDao.updateStatusByServerId(
            serverId = payload.msgId,
            status   = newStatus,
        )
    }

    // ─────────────────────────────────────────────
    // Core upsert logic
    // ─────────────────────────────────────────────

    /**
     * Smart upsert for any message that arrives from the network
     * (WebSocket or REST refresh) and therefore carries a serverId
     * but no clientId.
     *
     * - Row already exists with this serverId  → update status only.
     *   (This is our own optimistic row after mapClientToServer linked it.)
     * - Row does not exist                     → insert as a new message
     *   (Truly incoming message from another user, or a message we missed
     *    while offline.)
     */
    private suspend fun upsertIncomingMessage(entity: MessageEntity) {
        val serverId = entity.serverId ?: run {
            // No serverId means we can't deduplicate — just insert.
            messageDao.insertMessages(listOf(entity))
            return
        }

        val exists = messageDao.countByServerId(serverId) > 0
        if (exists) {
            // Row is already present (our own sent message).
            // Only update the status; don't overwrite local fields.
            messageDao.updateStatusByServerId(serverId, entity.status)
        } else {
            // Genuinely new row.
            messageDao.insertMessages(listOf(entity))
        }
    }

    // ─────────────────────────────────────────────
    // Message queries
    // ─────────────────────────────────────────────

    override fun getChats(chatId: Int): Flow<NetworkResult<List<MessageWithUser>>> {
        return messageDao.getMessagesWithUser(chatId)
            .map<List<MessageWithUser>, NetworkResult<List<MessageWithUser>>> { list ->
                NetworkResult.Success(list)
            }
            .catch { e ->
                emit(NetworkResult.Error(e.message ?: "DB error"))
            }
    }

    /**
     * Fetches a page of messages from the server and merges them into the
     * local database without creating duplicates.
     *
     * Uses [upsertIncomingMessage] for each DTO so that:
     *  - Messages we sent (already in DB with a serverId) are not duplicated.
     *  - Messages we missed while offline are inserted fresh.
     */
    override suspend fun refreshChats(chatId: Int) {
        val result = safeApiCall { api.getMessages(chatId, limit, nextCursor) }

        if (result is NetworkResult.Success) {
            val userId = dataStore.getUserId()

            result.data?.let { response ->
                nextCursor = response.nextCursor

                response.users
                    ?.find { it.id != userId }
                    ?.let { user ->
                        userDao.upsertUsers(
                            UserEntity(
                                id         = user.id,
                                name       = user.name,
                                profileUrl = user.profile_pic,
                                chatId     = user.chat_id,
                            )
                        )
                    }

                response.messages
                    ?.let { dtoList -> mapper.dtoListToEntityList(dtoList) }
                    ?.forEach { entity -> upsertIncomingMessage(entity) }
            }
        }
    }

    // ─────────────────────────────────────────────
    // Sending messages
    // ─────────────────────────────────────────────

    /**
     * Full send flow:
     *
     * 1. Insert an optimistic local row immediately (status = SENDING).
     *    The row is identified by [clientId] (a UUID the caller generated).
     *    serverId is null at this point.
     *
     * 2. POST to the API.
     *
     * 3a. On success → call [mapClientToServer] which fills in the real
     *     serverId and flips the status to SENT — all in one UPDATE.
     *     Now the row is identifiable by both clientId AND serverId, so
     *     subsequent WebSocket/receipt events won't create a duplicate.
     *
     * 3b. On failure → mark the row FAILED by clientId so the UI can
     *     surface a retry option.
     */

     override suspend fun uploadMedia( uri: Uri,chatId: Int,type:String){
        val result=cloudinaryService.upload(context,uri)
        result.secureUrl?.let {
            val request= MediaUploadRequest(
                chat_id = chatId,
                type = type,
                url = it,
                mime_type = result.mimeType,
                size_bytes = result.bytes.toString(),
                sha256 = result.sha256
            )
            when (val result = safeApiCall { api.uploadMedia(request) }){

                is NetworkResult.Success->{
                    val response = result.data ?: return
                    val request=SendMessageRequest(
                        chat_id = response.chat_id,
                        type = response.type,
                        body = response.url,
                        client_id = UUID.randomUUID().toString(),
                        media_id = response.id,
                        reply_to_id = null,
                        is_forwarded = false
                    )
                    val userId = dataStore.getUserId() ?: return
                    sendMessage(request, userId)

                }

                is NetworkResult.Error->{}
                else -> Unit
            }
        }
    }
    override suspend fun sendMessage(
        request: SendMessageRequest,
        currentUserId: Int,
    ) {
        val localMsg = mapper.createLocalMessage(
            chatId    = request.chat_id,
            senderId  = currentUserId,
            type      = request.type,
            body      = request.body,
            mediaId   = request.media_id,
            replyToId = request.reply_to_id,
            clientId  = request.client_id,
        )
        messageDao.insertMessages(listOf(localMsg))

        when (val result = safeApiCall { api.sendMessage(request) }) {

            is NetworkResult.Success -> {
                val response = result.data ?: return
                messageDao.mapClientToServer(
                    clientId = request.client_id,
                    serverId = response.id,
                    status   = MessageStatus.SENT,
                )
            }

            is NetworkResult.Error -> {
                messageDao.updateStatusByClientId(
                    clientId = request.client_id,
                    status   = MessageStatus.FAILED,
                )
            }

            else -> Unit
        }
    }


    override  suspend fun getChatUser(chatId: Int): Flow<UserUi?> {
        return userDao.getUserForChat(chatId)
            .map { entity ->
                entity?.let { mapper.toUserUi(it) }
            }
    }


    // ─────────────────────────────────────────────
    // Typing indicators (fire-and-forget)
    // ─────────────────────────────────────────────

    override fun sendTyping(chatId: Int) {
        wsManager.sendTyping(chatId)
    }


    override fun sendStopTyping(chatId: Int){
        wsManager.sendStopTyping(chatId)
    }
}