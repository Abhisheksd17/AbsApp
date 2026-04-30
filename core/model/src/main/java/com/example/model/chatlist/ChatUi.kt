package com.example.model.chatlist

data class ChatUi(
    val id: Int,
    val name: String,
    val message: String,
    val time: String,
    val unread: Boolean,
    val count: String
)