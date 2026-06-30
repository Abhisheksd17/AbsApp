package com.example.domain.repository

import android.net.Uri
import com.example.domain.data.NetworkResult
import com.example.model.message.MessageWithUser
import com.example.model.message.SendMessageRequest
import com.example.model.message.UserUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MessageRepository {
    fun getChats(
        chatId: Int,
    ): Flow<NetworkResult<List<MessageWithUser>>>

    suspend fun refreshChats(
        chatId: Int,
    )

    suspend fun sendMessage(
        request: SendMessageRequest,
        currentUserId: Int
    )

    suspend fun getChatUser(chatId: Int): Flow<UserUi?>

    suspend fun uploadMedia( uri: Uri,chatId: Int,type:String)

    val isTyping: StateFlow<Boolean>

    val isOnline:StateFlow<Boolean>

    fun sendTyping(chatId: Int)

    fun sendStopTyping(chatId: Int)
}