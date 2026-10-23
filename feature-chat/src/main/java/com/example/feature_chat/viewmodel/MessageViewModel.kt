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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import android.net.Uri
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.model.websocket.WsEvent
import com.example.worker.MessageSyncWorker
import com.example.worker.UploadMediaSyncWorker


@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val workManager: WorkManager,
    private val dataStore: DataStore
): ViewModel(){

    var userId: Int? = 0

    private val _chatId = MutableStateFlow<Int?>(null)
    val chatId: StateFlow<Int?> = _chatId

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatState: StateFlow<NetworkResult<List<MessageWithUser>>> = _chatId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(NetworkResult.Loading())
            } else {
                repository.getChats(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkResult.Loading()
        )

    val isTyping = repository.isTyping

    val isOnline = repository.isOnline

    init {
        viewModelScope.launch {
            userId = dataStore.getUserId()
        }
    }

    fun setChatId(id: Int) {
        if (_chatId.value != id) {
            _chatId.value = id
            refreshChats(id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatUser: StateFlow<UserUi?> = _chatId.filterNotNull().flatMapLatest { id ->
                repository.getChatUser(id)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun getChatMessage(chatId: Int) {
        setChatId(chatId)
    }

    fun refreshChats(chatId: Int){
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshChats(chatId)
            } finally {
                _isRefreshing.value = false
            }
        }
    }


    fun sendMessage(messageRequest: SendMessageRequest){
        val inputData = workDataOf(
            "chat_id" to messageRequest.chat_id,
            "type" to messageRequest.type,
            "body" to messageRequest.body,
            "client_id" to messageRequest.client_id,
            "media_id" to (messageRequest.media_id ?: -1),
            "reply_to_id" to (messageRequest.reply_to_id ?: -1),
            "is_forwarded" to messageRequest.is_forwarded,
            "userId" to (userId ?: 0)
        )
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<MessageSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(request)
    }

    fun uploadMedia( uri: Uri,chatId: Int,type:String){
        val inputData = workDataOf(
            "uri" to uri.toString(),
            "chatId" to chatId,
            "type" to type
        )
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request=OneTimeWorkRequestBuilder<UploadMediaSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        workManager.enqueue(request)

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