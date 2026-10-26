package com.example.feature_chat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.util.Utils
import com.example.domain.data.NetworkResult
import com.example.feature_chat.ui.AiChatTopBar
import com.example.feature_chat.ui.ChatBubble
import com.example.feature_chat.ui.ChatMessage
import com.example.feature_chat.ui.MessageInputBar
import com.example.feature_chat.viewmodel.AiViewModel
import com.example.model.message.MessageStatus
import com.example.ui.theme.Black
import com.example.ui.theme.BubbleIncoming
import com.example.ui.theme.IceGray
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TealGreen
import com.example.ui.theme.TextIncoming
import com.example.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun AiConversation() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    val viewModel: AiViewModel = hiltViewModel()
    val messagesState by viewModel.message.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val currentConversationId by viewModel.conversationId.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = White,
                modifier = Modifier.width(300.dp)
            ) {
                val pinnedList = conversations.filter { it.isPinned }
                val recentList = conversations.filter { !it.isPinned }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "AbsBot History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    )

                    HorizontalDivider(color = IceGray, modifier = Modifier.padding(bottom = 12.dp))

                    NavigationDrawerItem(
                        label = { Text("New Chat", fontWeight = FontWeight.SemiBold) },
                        selected = false,
                        onClick = {
                            viewModel.startNewChat()
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Chat",
                                tint = TealGreen
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = IceGray
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (pinnedList.isNotEmpty()) {
                            item {
                                Text(
                                    text = "PINNED CHATS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGray,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                                )
                            }
                            items(pinnedList, key = { "pinned_" + it.conversationId }) { item ->
                                val isSelected = item.conversationId == currentConversationId
                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = item.conversationName.ifBlank { "Conversation" },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.selectConversation(item.conversationId)
                                        scope.launch { drawerState.close() }
                                    },
                                    badge = {
                                        IconButton(
                                            onClick = {
                                                viewModel.togglePinConversation(item.conversationId, false)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = "Unpin",
                                                tint = TealGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = IceGray,
                                        selectedTextColor = TealGreen
                                    )
                                )
                            }
                        }

                        if (recentList.isNotEmpty()) {
                            item {
                                Text(
                                    text = "RECENT CHATS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGray,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                                )
                            }
                            items(recentList, key = { "recent_" + it.conversationId }) { item ->
                                val isSelected = item.conversationId == currentConversationId
                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            text = item.conversationName.ifBlank { "Conversation" },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.selectConversation(item.conversationId)
                                        scope.launch { drawerState.close() }
                                    },
                                    badge = {
                                        IconButton(
                                            onClick = {
                                                viewModel.togglePinConversation(item.conversationId, true)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.PushPin,
                                                contentDescription = "Pin",
                                                tint = SlateGray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = IceGray,
                                        selectedTextColor = TealGreen
                                    )
                                )
                            }
                        }

                        if (pinnedList.isEmpty() && recentList.isEmpty()) {
                            item {
                                Text(
                                    text = "No conversation history yet",
                                    fontSize = 14.sp,
                                    color = SlateGray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            topBar = {
                AiChatTopBar(
                    onSideClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
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
                    onMediaPickedWithCaption = { pickedMedia, caption ->
                        val file = Utils.uriToFile(context, pickedMedia.uri)
                        val text = caption.trim()
                        viewModel.sendAttachments(file = file, messageText = text.ifBlank { null })
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

                        LaunchedEffect(messageList.size, isSending) {
                            val totalCount = messageList.size + (if (isSending) 1 else 0)
                            if (totalCount > 0) {
                                listState.animateScrollToItem(totalCount - 1)
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(messageList, key = { it.messageId }) { item ->
                                val isSelf = item.role.equals("user", ignoreCase = true)
                                val formattedTime = Utils.formatTime(item.timestamp)
                                val chatMessage = ChatMessage(
                                    id = null,
                                    senderId = if (isSelf) 1 else 0,
                                    senderName = if (isSelf) "You" else "AI",
                                    text = item.message,
                                    timestamp = formattedTime,
                                    dateStamp = "",
                                    status = if (item.status.equals("ERROR", ignoreCase = true)) MessageStatus.FAILED else MessageStatus.SENT
                                )
                                ChatBubble(
                                    message = chatMessage,
                                    isSelf = isSelf
                                )
                            }

                            if (isSending) {
                                item(key = "ai_thinking_indicator") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 4.dp,
                                                        topEnd = 16.dp,
                                                        bottomStart = 16.dp,
                                                        bottomEnd = 16.dp
                                                    )
                                                )
                                                .background(BubbleIncoming)
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(14.dp),
                                                    strokeWidth = 2.dp,
                                                    color = TextIncoming
                                                )
                                                Text(
                                                    text = "AbsBot is thinking...",
                                                    color = TextIncoming,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
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
}
