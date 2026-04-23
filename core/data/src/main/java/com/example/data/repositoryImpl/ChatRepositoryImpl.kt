package com.example.data.repositoryImpl

import com.example.data.mapper.ChatListMapper
import com.example.data.wrapper.BaseApiResponse
import com.example.database.dao.ChatDao
import com.example.database.entity.ChatListEntity
import com.example.domain.data.ChatList
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ChatRepository
import com.example.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: ChatDao,
    private val mapper: ChatListMapper
) : ChatRepository, BaseApiResponse() {

    override fun getChats(): Flow<NetworkResult<List<ChatList>>> {
        return dao.observeChats()
            .map<List<ChatListEntity>, NetworkResult<List<ChatList>>> { entities ->
                val domainList = mapper.entityListToDomainList(entities)
                NetworkResult.Success(domainList)
            }
            .onStart {
                emit(NetworkResult.Loading<List<ChatList>>())
                refreshChats()
            }
            .catch { e ->
                emit(NetworkResult.Error<List<ChatList>>(e.message ?: "DB error"))
            }
    }
    override suspend fun refreshChats() {
        val result = safeApiCall { api.getChatList() }

        if (result is NetworkResult.Success) {
            result.data?.let { chatListResponse ->
                val entities = mapper.dtoToEntityList(chatListResponse)
                dao.insertChatList(entities)
            }
        }

    }
}