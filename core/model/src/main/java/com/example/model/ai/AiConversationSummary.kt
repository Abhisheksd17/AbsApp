package com.example.model.ai

data class AiConversationSummary(
    val conversationId: String,
    val conversationName: String,
    val lastUpdated: Long,
    val isPinned: Boolean = false,
)
