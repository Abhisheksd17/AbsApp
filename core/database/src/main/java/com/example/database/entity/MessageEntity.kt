package com.example.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.message.MessageStatus

@Entity(
    tableName = "messages",
    indices = [
        Index("chatId"),
        Index("senderId")
    ]
)
data class MessageEntity(
    @PrimaryKey val id: Int,
    val chatId: Int,
    val senderId: Int,
    val type: String,
    val body: String?,
    val mediaId: Int?,
    val mediaUrl: String?,
    val mediaThumb: String?,
    val replyToId: Int?,
    val createdAt: Long,
    val isForwarded: Boolean,
    val readByMe: Boolean,
    val clientId: String?,
    val status: MessageStatus
)