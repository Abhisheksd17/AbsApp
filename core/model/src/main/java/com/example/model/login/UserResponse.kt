package com.example.model.login

data class UserResponse(
    val id: Int,
    val display_name: String,
    val avatar_key: String?,
    val status_text: String?,
    val is_online: Boolean,
    val last_seen_at: Long?
)
