package com.example.model.message
import kotlinx.serialization.SerialName

data class SendMessageResponse(
    val id: Long,

    @SerialName("client_id")
    val clientId: String,

    @SerialName("created_at")
    val createdAt: Long
)
