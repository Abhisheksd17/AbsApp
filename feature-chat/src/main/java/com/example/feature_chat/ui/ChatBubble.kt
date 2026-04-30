package com.example.feature_chat.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BubbleIncoming
import com.example.ui.theme.BubbleOutgoing
import com.example.ui.theme.TextIncoming
import com.example.ui.theme.TextOutgoing
import com.example.ui.theme.TimeIncoming
import com.example.ui.theme.TimeOutgoing
import com.example.ui.theme.White



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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isSelf) 60.dp else 0.dp,
                end   = if (isSelf) 0.dp  else 60.dp,
                top   = 2.dp,
                bottom = 2.dp
            ),
        horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            when {
                message.isVoice -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.CircleShape)
                                .padding(4.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(100.dp)
                        ) {
                            val barHeights = listOf(8, 14, 10, 18, 12, 20, 10, 16, 8, 14, 10, 18)
                            barHeights.forEach { h ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(h.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.White.copy(alpha = 0.8f))
                                )
                            }
                        }

                        Column {
                            Text("00:16", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }

                else -> {
                    Column {
                        Text(
                            text = message.text,
                            color = textColor,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = message.timestamp,
                            color = timeColor,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}