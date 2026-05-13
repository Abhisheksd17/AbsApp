package com.example.model.call

data class IncomingCallEvent(
    val callId:    String,
    val callerId:  Int,
    val callerName: String = "",
    val callType:  String,
    val channel:   String,
)
