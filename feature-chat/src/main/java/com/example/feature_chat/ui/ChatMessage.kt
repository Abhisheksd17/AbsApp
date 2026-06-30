package com.example.feature_chat.ui

import com.example.model.message.MessageStatus
import com.example.model.message.MessageWithUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ChatMessage(
    val id: Int?,
    val senderId: Int,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val dateStamp: String,
    val isVoice: Boolean = false,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val audioUrl: String? = null,
    val replyTo: ChatMessage? = null,
    val status: MessageStatus,
)

fun MessageWithUser.toChatMessage() = ChatMessage(
    id         = serverId,
    senderId   = senderId,
    senderName = senderName ?: "Unknown",
    text       = if (type == "image" || type == "video" || type == "audio") "" else body ?: "",
    timestamp  = formatTimestamp(createdAt),
    dateStamp  = formatDateStamp(createdAt),
    isVoice    = type == "audio" || type == "voice",

    imageUrl   = if (type == "image") body else null,
    videoUrl   = if (type == "video") body else null,
    audioUrl   = if (type == "audio" || type == "voice") body else null,

    replyTo    = null,
    status     = status,
)

fun formatDateStamp(epochMillis: Long): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(Date(epochMillis))

}
fun formatTimestamp(epochMillis: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}