package com.example.feature_chat.ui

import com.example.model.message.MessageWithUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ChatMessage(
    val id: Int,
    val senderId: Int,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isVoice: Boolean = false,
    val imageUrl: String? = null,
    val replyTo: ChatMessage? = null
)
fun MessageWithUser.toChatMessage() = ChatMessage(
    id         = id,
    senderId   = senderId,
    senderName = senderName ?: "Unknown",
    text       = body ?: "",
    timestamp  = formatTimestamp(createdAt),
    replyTo    = null
)

fun formatTimestamp(epochMillis: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}