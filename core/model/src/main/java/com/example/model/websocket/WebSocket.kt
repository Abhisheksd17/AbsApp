package com.example.model.websocket


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsEnvelope(
    val event: String
)

@Serializable
data class WsNewMessage(
    val event: String,
    @SerialName("msg_id")    val msgId: Long,
    @SerialName("chat_id")   val chatId: Int,
    @SerialName("sender_id") val senderId: Int,
    val type: String,
    val body: String? = null,
    @SerialName("media_id")    val mediaId: Int? = null,
    @SerialName("media_url")   val mediaUrl: String? = null,
    @SerialName("media_thumb") val mediaThumb: String? = null,
    @SerialName("reply_to_id") val replyToId: Int? = null,
    @SerialName("created_at")  val createdAt: Long,
    @SerialName("is_forwarded") val isForwarded: Boolean = false,
)

@Serializable
data class WsReceipt(
    val event: String,
    @SerialName("msg_id") val msgId: Long,
    val recipient: Int,
    val status: String,
    val ts: Long,
)

@Serializable
data class WsTyping(
    val event: String,
    @SerialName("chat_id") val chatId: Int,
    @SerialName("user_id") val userId: Int,
)

@Serializable
data class WsIncomingCall(
    val call_id: String,
    val caller_id: Int,
    val caller_name: String = "",
    val call_type: String,
    val channel: String
)

@Serializable
data class WsCallAccepted(
    val call_id: String,
    val callee_id: Int,
    val channel: String
)

@Serializable
data class WsCallEnded(
    val call_id: String,
    val from: Int,
    val reason: String
)


@Serializable
data class WsCallSignal(
    val from: Int,
    val signal: String,
    val call_id: String
)