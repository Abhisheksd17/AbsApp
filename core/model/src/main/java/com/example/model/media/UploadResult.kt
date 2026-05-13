package com.example.model.media

data class UploadResult(
    val secureUrl: String,
    val bytes: Long,
    val mimeType: String,
    val sha256: String
)