package com.example.model.ai

data class ChatMediaRequest(
    val conversationId:String?,
    val message:String,
    val files:List<String>
)