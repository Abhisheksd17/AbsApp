package com.example.model.call

data class CallTokenResponse(
    val app_id:  String,
    val channel: String,
    val token:   String,
    val uid:     Int,
    val call_id: String,
)
