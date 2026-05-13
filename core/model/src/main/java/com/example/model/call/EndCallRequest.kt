package com.example.model.call

data class EndCallRequest(
    val peer_id: Int,
    val call_id: String,
    val reason: String = "ended"
)