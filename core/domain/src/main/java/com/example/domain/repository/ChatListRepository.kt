package com.example.domain.repository

import com.example.domain.data.ChatList
import com.example.domain.data.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ChatListRepository {

    fun getChatsList(): Flow<NetworkResult<List<ChatList>>>

    suspend fun refreshChatsList()
}