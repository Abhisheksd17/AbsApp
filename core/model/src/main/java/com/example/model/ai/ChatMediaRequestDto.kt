package com.example.model.ai

import java.io.File

data class ChatMediaRequestDto(
    val conversationId: String? = null,
    val message: String? = null,
    val files: List<File> = emptyList()
)