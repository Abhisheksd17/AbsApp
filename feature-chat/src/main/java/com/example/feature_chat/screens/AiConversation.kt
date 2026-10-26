package com.example.feature_chat.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.navigati.navigation.LocalNavigator
import com.example.common.util.Utils
import com.example.common.util.Utils.showSnackBar
import com.example.domain.data.NetworkResult
import com.example.feature_chat.ui.AiChatTopBar
import com.example.feature_chat.ui.ChatBubble
import com.example.feature_chat.ui.ChatMessage
import com.example.feature_chat.ui.MessageInputBar
import com.example.feature_chat.viewmodel.AiViewModel
import com.example.model.message.MessageStatus
import kotlinx.coroutines.launch

@Composable
fun AiConversation() {

    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val scope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    val viewModel: AiViewModel = hiltViewModel()
    val messagesState by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect { errorMessage ->
            showSnackBar(
                snackbarHostState,
                errorMessage
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            AiChatTopBar(
                onBackClick = {
                    scope.launch {
                        navigator.pop()
                    }
                }
            )
        },
        bottomBar = {
            MessageInputBar(
                message = messageText,
                onMessageChange = { messageText = it },
                onSend = {
                    val text = messageText.trim()
                    if (text.isNotBlank()) {
                        viewModel.sendMessage(text)
                        messageText = ""
                    }
                },
                onMediaPicked = { pickedMedia ->
                    val file = Utils.uriToFile(context, pickedMedia.uri)
                    viewModel.sendAttachments(file = file, messageText = messageText.ifBlank { null })
                    messageText = ""
                },
                onPhotoCaptured = { uri ->
                    val file = Utils.uriToFile(context, uri)
                    viewModel.sendAttachments(file = file, messageText = messageText.ifBlank { null })
                    messageText = ""
                },
                onVoiceRecorded = {}
            )
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            when (val state = messagesState) {
                is NetworkResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NetworkResult.Success -> {
                    val messageList = state.data ?: emptyList()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(messageList, key = { it.messageId }) { item ->
                            val isSelf = item.role.equals("user", ignoreCase = true)
                            val chatMessage = ChatMessage(
                                id = null,
                                senderId = if (isSelf) 1 else 0,
                                senderName = if (isSelf) "You" else "AI",
                                text = item.message,
                                timestamp = "",
                                dateStamp = "",
                                status = MessageStatus.SENT
                            )
                            ChatBubble(
                                message = chatMessage,
                                isSelf = isSelf
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Text(
                        text = state.message ?: "Failed to load messages",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )

                }
                is NetworkResult.Idle -> {}
            }
        }
    }
}