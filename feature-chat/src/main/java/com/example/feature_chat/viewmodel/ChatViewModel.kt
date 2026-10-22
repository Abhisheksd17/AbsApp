package com.example.feature_chat.viewmodel

import android.util.Log
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ChatRepository
import com.example.model.chat.CreateChatRequest
import com.example.model.chat.CreateChatResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    private val _createChatState = MutableStateFlow<NetworkResult<CreateChatResponse>>(NetworkResult.Idle())
    val createChatState = _createChatState.asStateFlow()

    private val _chatId = MutableStateFlow<Int?>(null)
    val chatId = _chatId.asStateFlow()

    fun createChat(userId: Int) {
        viewModelScope.launch {
            repository.createChat(CreateChatRequest(userId)).collect { response ->

                _createChatState.value = response

                if (response is NetworkResult.Success) {
                    val id = response.data?.chat_id
                    _chatId.value = id
                }
            }
        }
    }

    fun loadOrCreateChat(userId: Int) {
        viewModelScope.launch {
            val id = repository.getChatByUserId(userId)

            if (id != null) {
                _chatId.value = id
            } else {
                createChat(userId)
            }
        }
    }

}