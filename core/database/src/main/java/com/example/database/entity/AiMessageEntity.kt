package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_messages")
data class AiMessageEntity(
    @PrimaryKey
    val messageId: String,

    val conversationId: String,
    val role: String,
    val message: String,
    val status: String,
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
)
