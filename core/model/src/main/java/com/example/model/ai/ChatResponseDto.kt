package com.example.model.ai

data class ChatResponse(
    val conversationId:String,
    val messageId:String,
    val role:String,
    val message:String,
    val attachments:List<Attachment>,
    val status:String
)

data class Attachment(
    val id:String,
    val fileName:String,
    val mimeType:String,
    val size:String,
    val status:String
)
