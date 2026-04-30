package com.example.data.repositoryImpl

import android.util.Log
import com.example.common.datastore.DataStore
import com.example.data.mapper.ChatMapper
import com.example.data.wrapper.BaseApiResponse
import com.example.database.dao.MessageDao
import com.example.database.dao.UserDao
import com.example.database.entity.ChatEntity
import com.example.database.entity.UserEntity
import kotlinx.coroutines.flow.map
import com.example.domain.data.NetworkResult
import com.example.domain.repository.MessageRepository
import com.example.model.message.MessageStatus
import com.example.model.message.MessageWithUser
import com.example.model.message.SendMessageRequest
import com.example.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val messageDao: MessageDao,
    private val mapper: ChatMapper,
    private val userDao: UserDao,
    private val dataStore: DataStore
): MessageRepository, BaseApiResponse() {

    var nextCursor: String? = null
    val limit=20

    override fun getChats(
        chatId: Int,
    ): Flow<NetworkResult<List<MessageWithUser>>> {

        return messageDao.getMessagesWithUser(chatId)
            .map<List<MessageWithUser>, NetworkResult<List<MessageWithUser>>> { entities ->
                NetworkResult.Success(entities)
            }
            .onStart {
                emit(NetworkResult.Loading())
                refreshChats(chatId)
            }
            .catch { e ->
                emit(NetworkResult.Error(e.message ?: "DB error"))
            }
    }

    override suspend fun refreshChats(
        chatId: Int,
    ) {
        val result = safeApiCall { api.getMessages(chatId, limit, nextCursor) }
        if (result is NetworkResult.Success) {
            val userId = dataStore.getUserId()
            result.data?.let { response ->
                nextCursor = response.nextCursor

                response.users?.let { userList ->

                    userList.find { it.id != userId }?.let { user ->
                        val userEntity = UserEntity(
                            id = user.id,
                            name = user.name,
                            profileUrl = user.profile_url,
                            chatId=user.chat_id
                        )
                        userDao.upsertUsers(userEntity)
                    }
                }

                response.messages?.let { dtoList ->

                    val entities = mapper.dtoListToEntityList(dtoList)
                    messageDao.insertMessages(entities)

                }

            }
        }

    }

    override suspend fun sendMessage(
        request: SendMessageRequest,
        currentUserId: Int
    )
    {
        val localMsg = mapper.createLocalMessage(
            chatId = request.chat_id,
            senderId = currentUserId,
            type = request.type,
            body = request.body,
            mediaId = request.media_id,
            replyToId = request.reply_to_id,
            clientId = request.client_id
        )
        messageDao.insertMessages(listOf(localMsg))

        val result = safeApiCall { api.sendMessage(request) }

        when (result) {
            is NetworkResult.Success -> {

                val response = result.data
                messageDao.updateMessageStatus(
                    clientId = request.client_id,
                    status = MessageStatus.SENT,

                    )
            }

            is NetworkResult.Error -> {

                messageDao.updateMessageStatus(
                    clientId = request.client_id,
                    status = MessageStatus.FAILED
                )
            }

            else -> Unit
        }
    }
}