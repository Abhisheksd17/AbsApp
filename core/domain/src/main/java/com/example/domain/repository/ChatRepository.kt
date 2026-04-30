package com.example.domain.repository

import com.example.domain.data.NetworkResult
import com.example.model.chat.CreateChatRequest
import com.example.model.chat.CreateChatResponse
import com.example.model.chat.CreateGroupRequest
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

     suspend fun createChat(
        request: CreateChatRequest
    ): Flow<NetworkResult<CreateChatResponse>>

    suspend fun getChatByUserId(userId: Int): Int?

    suspend fun createGroupChat(
        request: CreateGroupRequest
    ): Flow<NetworkResult<CreateChatResponse>>
}