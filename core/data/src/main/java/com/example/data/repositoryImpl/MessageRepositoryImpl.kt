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

    private val _isTyping = MutableStateFlow(false)
    override val isTyping = _isTyping.asStateFlow()

    private val _isOnline=MutableStateFlow(false)
    override val isOnline=_isOnline.asStateFlow()

    private var currentChatId: Int? = null
    private var nextCursor: String? = null
    private val limit = 20

    private fun consumeWsEvents() {
        repoScope.launch {
            wsManager.events.collect { event ->
                when (event) {
                    is WsEvent.NewMessage   -> handleIncomingMessage(event.payload)
                    is WsEvent.Receipt      -> handleReceipt(event.payload)
                    is WsEvent.Typing -> {
                        _isTyping.value = event.isTyping
                    }
                    is WsEvent.Connected ->{
                        _isOnline.value=true
                        Log.d("MsgRepo", "WS connected")
                    }
                    is WsEvent.Disconnected -> {
                        _isOnline.value=false
                        Log.d("MsgRepo", "WS disconnected")
                    }
                    else                    -> Unit
                }
            }
        }
    }

    private suspend fun handleIncomingMessage(payload: WsNewMessage) {
        val entity = MessageEntity(
            localId = 0,
            serverId = payload.msgId,
            clientId = null,
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

    private suspend fun upsertIncomingMessage(entity: MessageEntity) {
        val serverId = entity.serverId ?: run {
            messageDao.insertMessages(listOf(entity))
            return
        }

        val exists = messageDao.countByServerId(serverId) > 0
        if (exists) {
            messageDao.updateStatusByServerId(serverId, entity.status)
        } else {
            messageDao.insertMessages(listOf(entity))
        }
    }

    override fun getChats(chatId: Int): Flow<NetworkResult<List<MessageWithUser>>> {
        return messageDao.getMessagesWithUser(chatId)
            .map<List<MessageWithUser>, NetworkResult<List<MessageWithUser>>> { list ->
                NetworkResult.Success(list)
            }
            .catch { e ->
                emit(NetworkResult.Error(e.message ?: "DB error"))
            }
    }

    override suspend fun refreshChats(chatId: Int) {
        // Reset cursor if we are moving to a different chat
        if (currentChatId != chatId) {
            currentChatId = chatId
            nextCursor = null
        }

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
                    val sendRequest=SendMessageRequest(
                        chat_id = response.chat_id,
                        type = response.type,
                        body = response.url,
                        client_id = UUID.randomUUID().toString(),
                        media_id = response.id,
                        reply_to_id = null,
                        is_forwarded = false
                    )
                    val userId = dataStore.getUserId() ?: return
                    sendMessage(sendRequest, userId)
                }
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

    override suspend fun getChatUser(chatId: Int): Flow<UserUi?> {
        return userDao.getUserForChat(chatId)
            .map { entity ->
                entity?.let { mapper.toUserUi(it) }
            }
    }

    override fun sendTyping(chatId: Int) {
        wsManager.sendTyping(chatId)
    }

    override fun sendStopTyping(chatId: Int){
        wsManager.sendStopTyping(chatId)
    }
}
