package com.example.model.message


data class MessageWithUser(
    val localId: Long,
    val serverId: Int?,
    val clientId: String?,

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

    val senderName: String?,
    val senderProfile: String?,
    val senderOnline: Boolean?,
)