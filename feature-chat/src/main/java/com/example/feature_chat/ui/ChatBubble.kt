package com.example.feature_chat.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.ui.theme.BubbleIncoming
import com.example.ui.theme.BubbleOutgoing
import com.example.ui.theme.TextIncoming
import com.example.ui.theme.TextOutgoing
import com.example.ui.theme.TimeIncoming
import com.example.ui.theme.TimeOutgoing



@Composable
fun ChatBubble(
    message: ChatMessage,
    isSelf: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isSelf) BubbleOutgoing else BubbleIncoming
    val textColor   = if (isSelf) TextOutgoing   else TextIncoming
    val timeColor   = if (isSelf) TimeOutgoing   else TimeIncoming

    val bubbleShape = if (isSelf) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    var showImageViewer by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start  = if (isSelf) 60.dp else 0.dp,
                end    = if (isSelf) 0.dp  else 60.dp,
                top    = 2.dp,
                bottom = 2.dp
            ),
        horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .background(bubbleColor)
        ) {
            when {


                message.imageUrl != null -> {
                    Column {
                        AsyncImage(
                            model = message.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(220.dp)
                                .aspectRatio(4f / 3f)
                                .clip(bubbleShape)
                                .clickable { showImageViewer = true },
                            contentScale = ContentScale.Crop
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = message.timestamp, fontSize = 10.sp, color = timeColor)
                            MessageStatusIcon(status = message.status, isSelf = isSelf, timeColor = timeColor)
                        }
                    }
                }

                message.videoUrl != null -> {
                    Column(modifier = Modifier.width(220.dp)) {
                        VideoPlayerBubble(
                            videoUrl = message.videoUrl,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = message.timestamp, fontSize = 10.sp, color = timeColor)
                            MessageStatusIcon(status = message.status, isSelf = isSelf, timeColor = timeColor)
                        }
                    }
                }

                message.isVoice || message.audioUrl != null -> {
                    AudioMessageBubble(
                        audioUrl  = message.audioUrl ?: "",
                        isSelf    = isSelf,
                        timestamp = message.timestamp,
                        status    = message.status,
                        modifier  = Modifier.width(240.dp)
                    )
                }

                else -> {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = message.text,
                            color = textColor,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = message.timestamp, fontSize = 10.sp, color = timeColor)
                            MessageStatusIcon(status = message.status, isSelf = isSelf, timeColor = timeColor)
                        }
                    }
                }
            }
        }
    }

    if (showImageViewer && message.imageUrl != null) {
        FullScreenImageViewer(
            imageUrl  = message.imageUrl,
            onDismiss = { showImageViewer = false }
        )
    }
}