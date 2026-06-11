package com.example.feature_chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.datastore.DataStore
import com.example.domain.data.ChatList
import com.example.domain.data.ChatUiState
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ChatListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatListRepository: ChatListRepository,
): ViewModel() {

    val chatListState: StateFlow<ChatUiState> = chatListRepository.getChatsList()
        .map { chats -> ChatUiState.Success(chats) as ChatUiState }
        .catch { e -> emit(ChatUiState.Error(e.message ?: "Database Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChatUiState.Loading
        )

    private val _networkEvent = MutableSharedFlow<String>()
    val networkEvent = _networkEvent.asSharedFlow()

    fun fetchChatList() {
        viewModelScope.launch {
            val result = chatListRepository.refreshChatsList()
            if (result is NetworkResult.Error) {
                _networkEvent.emit(result.message ?: "Failed to update chats")
            }
        }
    }
}
