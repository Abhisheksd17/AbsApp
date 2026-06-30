package com.example.model.message

data class UserUi(
    val id: Int,
    val name: String,
    val profileUrl: String?,
    val chatId: Int,
    val isOnline: Boolean = false
)
