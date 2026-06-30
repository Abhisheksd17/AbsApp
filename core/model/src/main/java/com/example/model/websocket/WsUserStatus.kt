package com.example.model.websocket

import kotlinx.serialization.Serializable

@Serializable
data class WsUserStatus(
    val user_id: Int,
    val online: Boolean
)
