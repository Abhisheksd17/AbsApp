package com.example.feature_chat.screens
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.domain.data.NetworkResult
import com.example.feature_chat.ui.HomeTopBar
import com.example.feature_chat.ui.SwipeableChatItem
import com.example.feature_chat.viewmodel.ChatListViewModel
import com.example.ui.theme.Black
import com.example.ui.theme.White

@Composable
fun Chat() {

    val viewModel: ChatListViewModel = hiltViewModel()
    val state by viewModel.chatListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getChatList()
    }

    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            HomeTopBar(
                search = false,
                query = "",
                onQueryChange = {},
                onSearchClick = {},
                onBackClick = {}
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(White)
            ) {

                when (val result = state) {

                    is NetworkResult.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is NetworkResult.Success -> {

                        val chats = result.data ?: emptyList()

                        LazyColumn {
                            items(chats, key = { it.chatId }) { item ->

                                SwipeableChatItem(
                                    imageUri = item.profileUrl ?: "",
                                    onImageSelected = {},
                                    name = item.title,
                                    message = item.lastMsgPreview ?: "",
                                    timesAgo = item.lastMsgAt?.toString() ?: "",
                                    unreadMsg = item.unreadCount > 0,
                                    msgCount = item.unreadCount,
                                    onMute = {},
                                    onDelete = {}
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(result.message ?: "Error")
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}