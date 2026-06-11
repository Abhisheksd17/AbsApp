package com.example.data.repositoryImpl

import com.example.common.datastore.DataStore
import com.example.data.mapper.ChatListMapper
import com.example.data.wrapper.BaseApiResponse
import com.example.data.wrapper.WebSocketManager
import com.example.database.dao.ChatListDao
import com.example.database.entity.ChatListEntity
import com.example.domain.data.ChatList
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ChatListRepository
import com.example.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatListRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: ChatListDao,
    private val mapper: ChatListMapper,
) : ChatListRepository, BaseApiResponse() {



    override fun getChatsList(): Flow<List<ChatList>> {
        return dao.observeChats()
            .map { mapper.entityListToDomainList(it) }
    }


    override suspend fun refreshChatsList(): NetworkResult<Unit> {
        val result = safeApiCall { api.getChatList() }
        if (result is NetworkResult.Success) {
            result.data?.let { dto ->
                dao.insertChatList(mapper.dtoToEntityList(dto))
            }
            return NetworkResult.Success(Unit)
        }
        return NetworkResult.Error(result.message ?: "Network Fetch Failed")
    }



}