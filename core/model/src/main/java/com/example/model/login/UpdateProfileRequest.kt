package com.example.model.login


data class UpdateProfileRequest(
    val display_name: String? = null,
    val status_text: String? = null,
    val avatar_key: String? ? = null
)