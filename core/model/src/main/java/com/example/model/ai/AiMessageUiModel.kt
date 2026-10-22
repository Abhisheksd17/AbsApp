package com.example.model.ai

data class AiMessageUiModel(
    val messageId: String,
    val conversationId: String,
    val role: String,
    val message: String,
    val status: String,
    val attachments: List<AiAttachmentUiModel> = emptyList()
)

data class AiAttachmentUiModel(
    val id: String,
    val filename: String,
    val mimetype: String,
    val size: Long,
    val status: String
)