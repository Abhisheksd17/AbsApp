package com.example.model.message

data class MessagesResponse(
    val messages: List<MessageDto>,
    val nextCursor: String?,
    val users: List<UserDto>?
)

