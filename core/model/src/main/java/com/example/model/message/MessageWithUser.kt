package com.example.model.message

data class MessageWithUser(
    val id: Int,
    val chatId: Int,
    val senderId: Int,
    val type: String,
    val body: String?,
    val mediaUrl: String?,
    val mediaThumb: String?,
    val replyToId: Int?,
    val createdAt: Long,
    val isForwarded: Boolean,

    val readByMe: Boolean,
    val status: MessageStatus,

    val senderName: String?,
    val senderProfile: String?,
    val senderOnline: Boolean?
)