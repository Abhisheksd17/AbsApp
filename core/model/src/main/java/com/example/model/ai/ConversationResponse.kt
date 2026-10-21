package com.example.model.ai

data class ConversationResponse(
    val conversationId: String,
    val messages: List<ChatResponse>,
    val total: Int,
    val limit: Int,
    val offset: Int
)