package com.example.domain.repository

import com.example.domain.data.NetworkResult
import com.example.model.ai.AiMessageUiModel
import com.example.model.ai.ChatMediaRequestDto
import com.example.model.ai.ChatRequestDto

interface AiRepository {

     suspend fun getAiMessage(conversation: String): NetworkResult<List<AiMessageUiModel>>

    suspend fun sendAiMessage(chatRequestDto: ChatRequestDto):NetworkResult<Unit>
    suspend fun sendAiAttachments(chatRequest: ChatMediaRequestDto):NetworkResult<Unit>
    suspend fun deleteAiMessage(conversationId:String):NetworkResult<Unit>

}