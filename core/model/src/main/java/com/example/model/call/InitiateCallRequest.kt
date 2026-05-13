package com.example.model.call

data class InitiateCallRequest(
    val callee_id: Int,
    val call_type: String = "video"
)

