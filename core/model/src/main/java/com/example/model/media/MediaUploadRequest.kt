package com.example.model.media

data class MediaUploadRequest(
    val chat_id: Int,
    val type: String,
    val url: String?,
    val mime_type: String,
    val size_bytes: String,
    val sha256: String
)
