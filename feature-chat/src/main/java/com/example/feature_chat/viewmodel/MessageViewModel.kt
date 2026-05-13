package com.example.feature_chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.datastore.DataStore
import com.example.domain.data.NetworkResult
import com.example.domain.repository.MessageRepository
import com.example.model.message.MessageWithUser
import com.example.model.message.SendMessageRequest
import com.example.model.message.UserUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import android.net.Uri
import com.example.model.websocket.WsEvent


@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val dataStore: DataStore
): ViewModel(){

    private val _chatState = MutableStateFlow<NetworkResult<List<MessageWithUser>>>(NetworkResult.Idle())
    val chatState = _chatState.asStateFlow()

    var userId:Int?=0


    private val _chatId = MutableStateFlow<Int?>(null)
    val chatId: StateFlow<Int?> = _chatId

    val isTyping=repository.isTyping



    fun setChatId(id: Int) {
        _chatId.value = id
    }

    val chatUser: StateFlow<UserUi?> = _chatId.filterNotNull().flatMapLatest { id ->
                repository.getChatUser(id)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    fun getChatMessage(chatId: Int){
        viewModelScope.launch {
            repository.getChats(chatId).collect{ response ->
                _chatState.value = response
            }
        }
    }

    fun refreshChats(chatId: Int){
        viewModelScope.launch {
            repository.refreshChats(chatId)
        }
    }

    fun sendMessage(messageRequest: SendMessageRequest){
        viewModelScope.launch {
            repository.sendMessage(messageRequest,userId?:0)
        }
    }

    fun uploadMedia( uri: Uri,chatId: Int,type:String){
        viewModelScope.launch {
            repository.uploadMedia(uri,chatId,type)
        }
    }

    fun sendTyping(chatId: Int){
        viewModelScope.launch {
            repository.sendTyping(chatId)
        }
    }

    fun sendStopTyping(chatId: Int){
        viewModelScope.launch {
            repository.sendStopTyping(chatId)
        }
    }



}