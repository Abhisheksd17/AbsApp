package com.example.model.call
import kotlinx.serialization.Serializable

data class CallParams(
    val appId:     String,
    val channel:   String,
    val token:     String,
    val uid:       Int,
    val callId:    String,
    val peerId:    Int,
    val peerName:  String,
    val isVideo:   Boolean = true,
    val isCaller:  Boolean,
)