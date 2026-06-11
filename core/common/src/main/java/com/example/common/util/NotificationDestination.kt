package com.example.common.util

sealed class NotificationDestination {
    data class OpenConversation(val userId: Int) : NotificationDestination()
    data class OpenCall(val params: String)      : NotificationDestination()
}