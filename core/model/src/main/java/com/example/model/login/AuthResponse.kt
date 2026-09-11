package com.example.model.login

data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val user_id: Int,
    val display_name: String,
    val status_text: String?,
    val profile_url: String?,
    val is_profile_completed : Boolean,
)
