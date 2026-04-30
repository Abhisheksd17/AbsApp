package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats_sender")
data class ChatEntity(
    @PrimaryKey val chatId: Int,   // server id
    val peerUserId: Int,           // other person
    val isSynced: Boolean = true,  // for offline support
    val createdAt: Long = System.currentTimeMillis()
)