package com.example.domain.repository

import com.example.domain.data.NetworkResult
import com.example.model.message.MessageWithUser
import com.example.model.message.SendMessageRequest
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getChats(
        chatId: Int,
    ): Flow<NetworkResult<List<MessageWithUser>>>

    suspend fun refreshChats(
        chatId: Int,
    )

    suspend fun sendMessage(
        request: SendMessageRequest,
        currentUserId: Int
    )
}