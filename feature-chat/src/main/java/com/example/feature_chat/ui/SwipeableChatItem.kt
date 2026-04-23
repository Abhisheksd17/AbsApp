package com.example.feature_chat.ui

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.R as ui
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableChatItem(
    imageUri: String?,
    onImageSelected: (Uri) -> Unit,
    name: String,
    message: String?,
    timesAgo: String?,
    unreadMsg: Boolean,
    msgCount: Int?,
    onMute: () -> Unit,
    onDelete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val revealWidth = 140f  // in px approx; will be overridden by measured dp->px below
    var revealWidthPx by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { } // placeholder; we measure button area below
    ) {
        // ── Background action buttons (rendered behind) ──
        SwipeActionsBackground(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .onSizeChanged { size ->
                    revealWidthPx = size.width.toFloat()
                },
            onMute = {
                coroutineScope.launch {
                    offsetX.animateTo(0f, tween(300)) // snap closed after action
                }
                onMute()
            },
            onDelete = {
                coroutineScope.launch {
                    offsetX.animateTo(0f, tween(300))
                }
                onDelete()
            }
        )

        // ── Foreground chat row (slides left on swipe) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                // Snap open if dragged past 40% of reveal width, else close
                                if (-offsetX.value > revealWidthPx * 0.5f) {
                                    offsetX.animateTo(-revealWidthPx, tween(300))
                                } else {
                                    offsetX.animateTo(0f, tween(300))
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, tween(300))
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                val newOffset = (offsetX.value + dragAmount)
                                    .coerceIn(-revealWidthPx, 0f) // clamp: no overscroll
                                offsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
        ) {
            ChatItem(
                imageUri = imageUri,
                onImageSelected = onImageSelected,
                name = name,
                message = message,
                timesAgo = timesAgo,
                unreadMsg = unreadMsg,
                msgCount = msgCount
            )
        }
    }
}

@Composable
fun SwipeActionsBackground(
    modifier: Modifier = Modifier,
    onMute: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bell / Mute button
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color.Black, CircleShape)
                .clickable { onMute() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(ui.drawable.notify_ic), // 🔔 use your mute icon
                contentDescription = "Mute",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Delete button
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFFE53935), CircleShape)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(ui.drawable.delete_ic),
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}