package com.example.model.ai

data class ChatMediaRequestDto(
    val conversationId:String?,
    val message:String,
    val files:List<String>
)