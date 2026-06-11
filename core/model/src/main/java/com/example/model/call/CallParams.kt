package com.example.model.call
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.net.URLDecoder

@Serializable
data class CallParams(
    val appId:    String,
    val channel:  String,
    val token:    String,
    val uid:      Int,
    val callId:   String,
    val peerId:   Int,
    val peerName: String,
    val isVideo:  Boolean = true,
    val isCaller: Boolean,
) {
    fun toRoute(): String =
        URLEncoder.encode(Json.encodeToString(this), "UTF-8")

    companion object {
        fun fromRoute(raw: String): CallParams? = runCatching {
            Json.decodeFromString<CallParams>(
                URLDecoder.decode(raw, "UTF-8")
            )
        }.getOrNull()
    }
}