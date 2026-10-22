package com.example.model.ai

data class AllConversationResponse(
    val conversations: List<Conversation>,
    val total: Int,
    val limit: Int,
    val offset: Int
)

data class Conversation(
    val conversationId: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val messageCount: Int
)