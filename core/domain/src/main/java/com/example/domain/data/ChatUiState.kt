package com.example.domain.data

sealed interface ChatUiState {
    object Loading : ChatUiState
    data class Success(val chats: List<ChatList>, val isRefreshing: Boolean = false) : ChatUiState
    data class Error(val message: String) : ChatUiState
}