package com.example.model.message

data class MessageDto(
    val id: Long,
    val chat_id: Int,
    val sender_id: Int,
    val type: String,
    val body: String?,
    val media_id: Int?,
    val media_url: String?,
    val media_thumb: String?,
    val reply_to_id: Int?,
    val created_at: Long,
    val is_forwarded: Boolean,
    val read_by_me: Boolean
)