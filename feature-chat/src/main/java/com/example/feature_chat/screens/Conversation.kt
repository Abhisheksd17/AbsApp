package com.example.feature_chat.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.util.Utils.showSnackBar
import com.example.common.viewmodel.CallerViewModel
import com.example.domain.data.CallState
import com.example.domain.data.NetworkResult
import com.example.feature_chat.ui.*
import com.example.feature_chat.viewmodel.ChatViewModel
import com.example.feature_chat.viewmodel.MessageViewModel
import com.example.model.call.CallParams
import com.example.model.message.SendMessageRequest
import com.example.ui.theme.SlateGray
import com.example.common.R as text
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun Conversation(userId: Int?,
                 onNavigateToCall: (CallParams) -> Unit) {

    val viewModel: MessageViewModel = hiltViewModel()
    val state       by viewModel.chatState.collectAsState()
    val chatViewModel: ChatViewModel = hiltViewModel()
    val chatId      by chatViewModel.chatId.collectAsState()
    val chatUser    by viewModel.chatUser.collectAsState()
    val context     = LocalContext.current
    val callVm: CallerViewModel = hiltViewModel()
    val callState by callVm.callState.collectAsState()

    val messageText = remember { mutableStateOf("") }
    var replyingTo  by remember { mutableStateOf<ChatMessage?>(null) }
    val listState   = rememberLazyListState()
    val scope       = rememberCoroutineScope()
    val Typing = remember { mutableStateOf(false) }
    val isTyping by viewModel.isTyping.collectAsState()
    val isOnline = chatUser?.isOnline ?: false
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val messages = when (val s = state) {

        is NetworkResult.Success -> s.data ?: emptyList()
        else -> emptyList()
    }

    val reversedMessages = remember(messages) {
        messages.reversed()
    }

    LaunchedEffect(Unit) {
        if (reversedMessages.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(userId) {
        userId?.let {
            chatViewModel.loadOrCreateChat(it)
        }
    }

    LaunchedEffect(chatId) {
        chatId?.let {
            viewModel.setChatId(it)
            viewModel.refreshChats(it)
            viewModel.getChatMessage(it)
        }
    }

    var isLoadingMore by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.isScrollInProgress to listState.firstVisibleItemIndex
        }
            .collect { (isScrolling, index) ->


                // Only trigger when user is actively scrolling
                if (!isScrolling) return@collect

                val shouldLoadMore = index >= messages.lastIndex - 3

                if (shouldLoadMore && !isLoadingMore && messages.isNotEmpty()) {
                    isLoadingMore = true


                    chatId?.let {
                        viewModel.refreshChats(it)
                    }
                }
            }
    }

    LaunchedEffect(messages.size) {
        isLoadingMore = false
    }

    LaunchedEffect(reversedMessages.size) {
        if (reversedMessages.isNotEmpty() && listState.layoutInfo.totalItemsCount > 0) {
            scope.launch {
                listState.scrollToItem(0)
            }
        }
    }

    LaunchedEffect(messageText.value) {
        val cId = chatId ?: return@LaunchedEffect

        if (messageText.value.isNotEmpty()) {
            if (!Typing.value) {
                Typing.value = true
                viewModel.sendTyping(cId)
            }

        } else {
            if (Typing.value) {
                Typing.value = false
                viewModel.sendStopTyping(cId)
            }
        }
    }

    LaunchedEffect(callState) {
        when (val state = callState) {
            is CallState.Ringing -> {
                onNavigateToCall(
                    state.params
                )
            }
            is CallState.Error -> {
                showSnackBar(
                    snackbarHostState,
                    state.message
                )
            }
            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            ChatTopBar(
                imageUri     = chatUser?.profileUrl ?: "",
                name         = chatUser?.name ?: "",
                status       = if (isTyping) "Typing..." else "",
                isOnline     = isOnline,
                onBackClick  = {},
                onCallClick  = {
                    callVm.initiateCall(
                        calleeId = userId?:0,
                        calleeName = chatUser?.name ?: "",
                        isVideo = false
                    )
                },
                onVideoClick = {
                    callVm.initiateCall(
                        calleeId = userId?:0,
                        calleeName = chatUser?.name ?: "",
                        isVideo = true
                    )
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ReplyPreviewBar(
                    replyMessage  = replyingTo,
                    currentUserId = viewModel.userId?.toInt() ?: 0,
                    onDismiss     = { replyingTo = null }
                )

                MessageInputBar(
                    message         = messageText.value,
                    onMessageChange = { messageText.value = it },
                    onSend = {
                        val text = messageText.value.trim()
                        val cId  = chatId ?: return@MessageInputBar
                        if (text.isBlank()) return@MessageInputBar
                        viewModel.sendMessage(
                            SendMessageRequest(
                                chat_id      = cId,
                                type         = "text",
                                body         = text,
                                client_id    = UUID.randomUUID().toString(),
                                media_id     = null,
                                reply_to_id  = replyingTo?.id,
                                is_forwarded = false
                            )
                        )
                        messageText.value = ""
                        replyingTo = null
                    },
                    onMediaPicked = { media ->
                        val cId = chatId ?: return@MessageInputBar
                        viewModel.uploadMedia(
                            uri = media.uri,
                            chatId = cId,
                            type = media.type
                        )
                        replyingTo = null
                    },
                    onPhotoCaptured = { uri ->
                        val cId = chatId ?: return@MessageInputBar
                        viewModel.uploadMedia(uri = uri, chatId = cId, type = "image")
                        replyingTo = null
                    },
                    onVoiceRecorded = { file ->
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                        val cId = chatId ?: return@MessageInputBar
                        viewModel.uploadMedia(uri = uri, chatId = cId, type = "audio")
                        replyingTo = null
                    },
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
                is NetworkResult.Loading -> {  }
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
                        Button(onClick = { chatId?.let { viewModel.getChatMessage(it) } }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    if (reversedMessages.isEmpty()) {
                        Text(
                            text     = stringResource(text.string.no_message),
                            modifier = Modifier.align(Alignment.Center),
                            style    = MaterialTheme.typography.bodyMedium,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else
                    {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            reverseLayout = true
                        )
                        {

                            itemsIndexed(
                                reversedMessages,
                                key = { _, message -> message.serverId ?: message.localId }
                            ) { index, message ->

                                val currentDate = formatDateStamp(message.createdAt)

                                val nextDate = reversedMessages
                                    .getOrNull(index + 1)
                                    ?.let { formatDateStamp(it.createdAt) }

                                val showDate =
                                    index == reversedMessages.lastIndex ||
                                            currentDate != nextDate



                                val isSelf = message.senderId == viewModel.userId

                                SwipeableMessageItem(
                                    onSwipeToReply = {
                                        replyingTo = message.toChatMessage()
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = if (isSelf) Alignment.End else Alignment.Start
                                    ) {

                                        message.replyToId?.let { replyId ->
                                            val original = messages.find { it.serverId == replyId }
                                            original?.let {
                                                ReplyQuoteChip(
                                                    originalMessage = it.toChatMessage(),
                                                    currentUserId = viewModel.userId ?: 0,
                                                    isSelfBubble = isSelf
                                                )
                                                Spacer(Modifier.height(2.dp))
                                            }
                                        }

                                        ChatBubble(
                                            message = message.toChatMessage(),
                                            isSelf = isSelf
                                        )
                                        if (showDate) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = currentDate,
                                                    color = SlateGray,
                                                    fontSize = 10.sp
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
    }
}