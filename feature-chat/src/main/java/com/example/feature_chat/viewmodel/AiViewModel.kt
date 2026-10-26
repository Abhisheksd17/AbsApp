package com.example.feature_chat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.data.NetworkResult
import com.example.domain.repository.AiRepository
import com.example.model.ai.AiConversationSummary
import com.example.model.ai.AiMessageUiModel
import com.example.model.ai.ChatMediaRequestDto
import com.example.model.ai.ChatRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _conversationId = MutableStateFlow(UUID.randomUUID().toString())
    val conversationId: StateFlow<String> = _conversationId.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    val conversations: StateFlow<List<AiConversationSummary>> =
        aiRepository.getAllConversations()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val message: StateFlow<NetworkResult<List<AiMessageUiModel>>> =
        _conversationId
            .flatMapLatest { id ->
                aiRepository.getAiMessage(id)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = NetworkResult.Idle()
            )

    fun startNewChat() {
        _conversationId.value = UUID.randomUUID().toString()
    }

    fun selectConversation(id: String) {
        if (id.isNotBlank()) {
            _conversationId.value = id
        }
    }

    fun togglePinConversation(conversationId: String, isPinned: Boolean) {
        viewModelScope.launch {
            aiRepository.togglePinConversation(conversationId, isPinned)
        }
    }

    fun setConversationId(id: String) {
        if (id.isNotBlank()) {
            _conversationId.value = id
        }
    }

    fun sendMessage(chatRequestDto: ChatRequestDto) {
        viewModelScope.launch {
            _isSending.value = true
            try {
                val currentId = chatRequestDto.conversationId?.takeIf { it.isNotBlank() }
                    ?: _conversationId.value.ifBlank {
                        UUID.randomUUID().toString().also { _conversationId.value = it }
                    }
                val request = chatRequestDto.copy(conversationId = currentId)
                when (val result = aiRepository.sendAiMessage(request)) {
                    is NetworkResult.Success -> {
                        result.data?.conversationId?.takeIf { it.isNotBlank() }?.let { backendId ->
                            _conversationId.value = backendId
                        }
                    }
                    is NetworkResult.Error -> {
                        _errorEvent.emit(result.message ?: "Failed to send message")
                    }
                    else -> Unit
                }
            } finally {
                _isSending.value = false
            }
        }
    }

    fun sendMessage(messageText: String) {
        if (messageText.isBlank()) return
        sendMessage(ChatRequestDto(conversationId = _conversationId.value, message = messageText))
    }

    fun sendAttachments(file: File, messageText: String? = null) {
        sendAttachments(
            ChatMediaRequestDto(
                conversationId = _conversationId.value,
                message = messageText,
                files = listOf(file)
            )
        )
    }

    fun sendAttachments(chatRequest: ChatMediaRequestDto) {
        viewModelScope.launch {
            _isSending.value = true
            try {
                val currentId = chatRequest.conversationId?.takeIf { it.isNotBlank() }
                    ?: _conversationId.value.ifBlank {
                        UUID.randomUUID().toString().also { _conversationId.value = it }
                    }
                val request = chatRequest.copy(conversationId = currentId)
                when (val result = aiRepository.sendAiAttachments(request)) {
                    is NetworkResult.Success -> {
                        result.data?.conversationId?.takeIf { it.isNotBlank() }?.let { backendId ->
                            _conversationId.value = backendId
                        }
                    }
                    is NetworkResult.Error -> {
                        _errorEvent.emit(result.message ?: "Failed to send attachments")
                    }
                    else -> Unit
                }
            } finally {
                _isSending.value = false
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            val result = aiRepository.deleteAiMessage(conversationId)
            if (result is NetworkResult.Error) {
                _errorEvent.emit(result.message ?: "Failed to delete conversation")
            }
        }
    }
}


