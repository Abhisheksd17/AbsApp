package com.example.model.message

data class SendMessageRequest(
    val chat_id: Int,
    val type: String,
    val body: String?,
    val client_id: String,
    val media_id: Int?,
    val reply_to_id: Int?,
    val is_forwarded: Boolean
)
