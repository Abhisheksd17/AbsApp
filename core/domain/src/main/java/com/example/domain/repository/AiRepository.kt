package com.example.domain.repository

import com.example.domain.data.NetworkResult
import com.example.model.ai.AiMessageUiModel
import com.example.model.ai.ChatMediaRequestDto
import com.example.model.ai.ChatRequestDto
import com.example.model.ai.ChatResponse
import kotlinx.coroutines.flow.Flow

interface AiRepository {

    fun getAiMessage(conversation: String = ""): Flow<NetworkResult<List<AiMessageUiModel>>>
    suspend fun sendAiMessage(chatRequestDto: ChatRequestDto): NetworkResult<ChatResponse>
    suspend fun sendAiAttachments(chatRequest: ChatMediaRequestDto): NetworkResult<ChatResponse>
    suspend fun deleteAiMessage(conversationId: String): NetworkResult<Unit>

}