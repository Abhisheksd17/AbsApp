package com.example.data.repositoryImpl

import com.example.data.wrapper.BaseApiResponse
import com.example.database.dao.UserDao
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ChatRepository
import com.example.model.chat.CreateChatRequest
import com.example.model.chat.CreateChatResponse
import com.example.model.chat.CreateGroupRequest
import com.example.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val userDao: UserDao
): ChatRepository, BaseApiResponse() {


    override suspend fun createChat(
        request: CreateChatRequest
    ): Flow<NetworkResult<CreateChatResponse>> {

        return flow {
            emit(NetworkResult.Loading())

            val result = safeApiCall {
                api.createDirectChat(request)
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun createGroupChat(
        request: CreateGroupRequest
    ): Flow<NetworkResult<CreateChatResponse>> {

        return flow {
            emit(NetworkResult.Loading())

            val result = safeApiCall {
                api.createGroupChat(request)
            }

            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getChatByUserId(userId: Int): Int? {
      return userDao.getChatId(userId)
    }
}