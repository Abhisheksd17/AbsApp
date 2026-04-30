package com.example.feature_chat.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.data.NetworkResult
import com.example.feature_chat.ui.*
import com.example.feature_chat.viewmodel.ChatViewModel
import com.example.feature_chat.viewmodel.MessageViewModel
import com.example.model.message.SendMessageRequest
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.toString

@Composable
fun Conversation(userId: Int?) {

    val viewModel: MessageViewModel = hiltViewModel()
    val state     by viewModel.chatState.collectAsState()
    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatId by chatViewModel.chatId.collectAsState()

    val messageText = remember { mutableStateOf("") }
    var replyingTo  by remember { mutableStateOf<ChatMessage?>(null) }
    val listState   = rememberLazyListState()
    val scope       = rememberCoroutineScope()

    val messages = when (val s = state) {
        is NetworkResult.Success -> s.data ?: emptyList()
        else -> emptyList()
    }
    LaunchedEffect(userId) {
        userId?.let {
            chatViewModel.loadOrCreateChat(it)
        }
    }


    LaunchedEffect(chatId) {
        chatId?.let {
            viewModel.getChatList(it)
        }
    }


    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                imageUri     = "",
                name         = "John Abraham",
                status       = "Active now",
                isOnline     = true,
                onBackClick  = {},
                onCallClick  = {},
                onVideoClick = {}
            )
        },
        bottomBar = {
            Column {
                ReplyPreviewBar(
                    replyMessage  = replyingTo,
                    currentUserId = viewModel.userId?.toInt()?:0,
                    onDismiss     = { replyingTo = null }
                )

                MessageInputBar(
                    message         = messageText.value,
                    onMessageChange = { messageText.value = it },
                    onAttachClick   = {},
                    onEmojiClick    = {},
                    onCameraClick   = {},
                    onMicClick      = {},
                    onSend          = {
                        val text   = messageText.value.trim()
                        val cId    = chatId ?: return@MessageInputBar
                        if (text.isBlank()) return@MessageInputBar

                        viewModel.sendMessage(
                            SendMessageRequest(
                                chat_id     = cId,
                                type        = "text",
                                body        = text,
                                client_id   = UUID.randomUUID().toString(),
                                media_id    = null,
                                reply_to_id = replyingTo?.id,
                                is_forwarded = false
                            )
                        )
                        messageText.value = ""
                        replyingTo        = null
                    }
                )
            }
        }
    ) { scaffoldPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            when (val s = state) {

                is NetworkResult.Loading -> {

                }

                is NetworkResult.Error -> {
                    Column(
                        modifier            = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text  = s.message ?: "Something went wrong",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = {
                                chatId?.let { viewModel.getChatList(it) }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    if (messages.isEmpty()) {
                        Text(
                            text     = "No messages yet. Say hello! 👋",
                            modifier = Modifier.align(Alignment.Center),
                            style    = MaterialTheme.typography.bodyMedium,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            state    = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                                .padding(horizontal = 8.dp),
                            contentPadding      = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(messages, key = { it.id }) { message ->

                                val isSelf = message.senderId == viewModel.userId

                                SwipeableMessageItem(
                                    onSwipeToReply = {
                                        replyingTo = message.toChatMessage()
                                    }
                                ) {
                                    Column(
                                        modifier            = Modifier.fillMaxWidth(),
                                        horizontalAlignment = if (isSelf) Alignment.End
                                        else        Alignment.Start
                                    ) {
                                        message.replyToId?.let { replyId ->
                                            val original = messages.find { it.id == replyId }
                                            original?.let {
                                                ReplyQuoteChip(
                                                    originalMessage = it.toChatMessage(),
                                                    currentUserId   = viewModel.userId?: 0,
                                                    isSelfBubble    = isSelf
                                                )
                                                Spacer(Modifier.height(2.dp))
                                            }
                                        }

                                        ChatBubble(
                                            message = message.toChatMessage(),
                                            isSelf  = isSelf
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}