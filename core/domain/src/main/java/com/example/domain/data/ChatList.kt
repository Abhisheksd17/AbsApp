package com.example.domain.data

data class ChatList(
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
