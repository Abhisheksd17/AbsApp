package com.example.model.chatlist

data class ChatDetails(
    val chat_id: Int,
    val type: String,
    val title: String,
    val user_id: Int,
    val profile_url: String?,
    val last_msg_preview: String?,
    val last_msg_at: Long?,
    val last_msg_sender_id: Int?,
    val unread_count: Int?,
    val peer_online: Boolean
)
