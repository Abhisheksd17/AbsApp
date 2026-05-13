package com.example.feature_chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.model.message.MessageStatus
import com.example.ui.theme.TimeIncoming
import com.example.ui.theme.TimeOutgoing
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Composable
fun AudioMessageBubble(
    audioUrl: String,
    isSelf: Boolean,
    timestamp: String,
    status: MessageStatus,
    modifier: Modifier = Modifier
) {
    val context     = LocalContext.current
    val iconColor   = if (isSelf) Color.White else Color(0xFF075E54)
    val waveColor   = if (isSelf) Color.White.copy(alpha = 0.8f) else Color(0xFF075E54).copy(alpha = 0.8f)
    val timeColor   = if (isSelf) TimeOutgoing else TimeIncoming

    var isPlaying       by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration        by remember { mutableStateOf(0L) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = false
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            duration        = exoPlayer.duration.coerceAtLeast(1L)
            delay(200)
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    isPlaying = false
                    currentPosition = 0L
                    exoPlayer.pause()   // ✅ explicitly pause
                    exoPlayer.seekTo(0) // reset to beginning
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    val progress  = if (duration > 0) currentPosition.toFloat() / duration else 0f
    val durationLabel = formatAudioDuration(if (duration > 0) duration else 0L)
    val barHeights    = listOf(8, 14, 10, 18, 12, 20, 10, 16, 8, 14, 10, 18, 9, 15, 11)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f))
                .clickable {
                    if (isPlaying) {
                        exoPlayer.pause()
                        isPlaying = false
                    } else {
                        exoPlayer.play()
                        isPlaying = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                barHeights.forEachIndexed { index, h ->
                    val barProgress = index.toFloat() / barHeights.size
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(h.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (barProgress <= progress) waveColor
                                else waveColor.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = durationLabel,
                    fontSize = 10.sp,
                    color = timeColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = timestamp, fontSize = 10.sp, color = timeColor)
                    MessageStatusIcon(status = status, isSelf = isSelf, timeColor = timeColor)
                }
            }
        }
    }
}

fun formatAudioDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes      = totalSeconds / 60
    val seconds      = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}