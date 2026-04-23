package com.example.feature_chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.data.ChatList
import com.example.domain.data.NetworkResult
import com.example.domain.repository.ChatRepository
import com.example.model.event.AuthEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel() {
    private val _chatListState = MutableStateFlow<NetworkResult<List<ChatList>>>(NetworkResult.Idle())
    val chatListState = _chatListState.asStateFlow()


    fun getChatList(){
        viewModelScope.launch {
            chatRepository.getChats().collect{ response ->
                _chatListState.value = response
            }
        }
    }

}


