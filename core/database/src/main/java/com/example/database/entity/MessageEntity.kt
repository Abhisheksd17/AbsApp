package com.example.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.message.MessageStatus

@Entity(
    tableName = "messages",
    indices = [
        Index("chatId"),
        Index("senderId"),
        Index(value = ["clientId"], unique = true),
        Index(value = ["serverId"], unique = true),
    ]
)
data class MessageEntity(

    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,

    val serverId: Long?,    // backend msg_id — null until API confirms send
    val clientId: String?,  // local UUID — null for messages from other users

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
    val status: MessageStatus,
)