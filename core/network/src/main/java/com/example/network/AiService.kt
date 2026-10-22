package com.example.network

import com.example.model.ApiResponse
import com.example.model.ai.AllConversationResponse
import com.example.model.ai.ChatMediaRequestDto
import com.example.model.ai.ChatRequestDto
import com.example.model.ai.ChatResponse
import com.example.model.ai.ConversationResponseDto
import com.example.model.ai.DeleteConversationResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AiService {

    @POST("/ai/chat")
    suspend fun chatWithAi(
        @Body request: ChatRequestDto
    ): retrofit2.Response<ApiResponse<ChatResponse>>

    @POST("/ai/chat/multimodal")
    suspend fun chatWithAiMultiModal(
        @Body request: ChatMediaRequestDto
    ): retrofit2.Response<ApiResponse<ChatResponse>>

    @POST("ai/conversations")
    suspend fun getAllConversations(): retrofit2.Response<ApiResponse<AllConversationResponse>>

    @GET("ai/conversations/messages")
    suspend fun getConversationMessages(
        @Query("conversation_id") conversationId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ):retrofit2.Response<ApiResponse<ConversationResponseDto>>


    @DELETE("ai/conversations")
    suspend fun deleteConversation(
        @Query("conversation_id") conversationId: String
    ): retrofit2.Response<ApiResponse<DeleteConversationResponse>>

}