package com.example.feature_chat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.datastore.DataStore
import com.example.domain.data.NetworkResult
import com.example.domain.repository.MessageRepository
import com.example.model.message.MessageWithUser
import com.example.model.message.SendMessageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val dataStore: DataStore
): ViewModel(){

    private val _chatState = MutableStateFlow<NetworkResult<List<MessageWithUser>>>(NetworkResult.Idle())
    val chatState = _chatState.asStateFlow()
    var userId:Int?=0

    init {
        viewModelScope.launch {
             userId = dataStore.getUserId()
        }
    }

    fun getChatList(chatId: Int){
        viewModelScope.launch {
            repository.getChats(chatId).collect{ response ->
                _chatState.value = response
            }
        }
    }

    fun sendMessage(messageRequest: SendMessageRequest){
        viewModelScope.launch {
            repository.sendMessage(messageRequest,userId?:0)
        }
    }

}