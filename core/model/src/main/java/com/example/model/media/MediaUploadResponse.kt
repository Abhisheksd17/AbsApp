package com.example.model.media

data class MediaUploadResponse(
    val id: Int,
    val chat_id: Int,
    val type: String,
    val url: String?,
    val thumb_url: String?,
    val created_at: Long
)
