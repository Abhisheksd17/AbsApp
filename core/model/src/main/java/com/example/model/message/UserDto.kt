package com.example.model.message

data class UserDto(
    val id: Int,
    val name: String,
    val profile_url: String?,
    val online: Boolean,
    val chat_id:Int
)
