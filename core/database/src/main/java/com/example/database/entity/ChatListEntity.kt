package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatListEntity(

    @PrimaryKey
    val chatId: Int,

    val title: String,
    val userId: Int?,
    val profileUrl: String?,
    val type: String,

    val lastMsgPreview: String?,
    val lastMsgAt: Long?,
    val lastMsgSenderId: Int?,

    val unreadCount: Int,
    val peerOnline: Boolean?
)