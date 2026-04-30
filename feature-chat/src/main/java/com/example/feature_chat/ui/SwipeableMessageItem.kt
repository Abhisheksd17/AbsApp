package com.example.feature_chat.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val SWIPE_THRESHOLD = 100f
private const val MAX_DRAG = 120f

@Composable
fun SwipeableMessageItem(
    onSwipeToReply: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    var rawOffset by remember { mutableFloatStateOf(0f) }
    // Only used for haptic, NOT for gating onSwipeToReply
    var hapticFired by remember { mutableStateOf(false) }

    val animatedOffset by animateFloatAsState(
        targetValue = rawOffset,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "swipe_offset"
    )

    val iconAlpha = (rawOffset / SWIPE_THRESHOLD).coerceIn(0f, 1f)
    val iconScale by animateFloatAsState(
        targetValue = if (rawOffset >= SWIPE_THRESHOLD) 1.2f
        else (rawOffset / SWIPE_THRESHOLD).coerceIn(0.5f, 1.2f),
        label = "icon_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // ✅ FIX 1: Fire reply here on release, not inside onHorizontalDrag
                        // This is the only place it should be called — once, cleanly
                        if (rawOffset >= SWIPE_THRESHOLD) {
                            onSwipeToReply()
                        }
                        rawOffset = 0f
                        hapticFired = false
                    },
                    onDragCancel = {
                        rawOffset = 0f
                        hapticFired = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()

                        // ✅ FIX 2: Ignore leftward drags that would go negative
                        if (dragAmount < 0f && rawOffset == 0f) return@detectHorizontalDragGestures

                        val newOffset = (rawOffset + dragAmount).coerceIn(0f, MAX_DRAG)
                        rawOffset = newOffset

                        // Haptic fires once at threshold crossing
                        if (!hapticFired && newOffset >= SWIPE_THRESHOLD) {
                            hapticFired = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } else if (hapticFired && newOffset < SWIPE_THRESHOLD) {
                            hapticFired = false
                        }
                    }
                )
            }
    ) {
        // ✅ FIX 3: Drive icon visibility with alpha instead of AnimatedVisibility
        // AnimatedVisibility with state from outside its own scope caused flicker/miss
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(36.dp)
                .scale(iconScale)
                .alpha(iconAlpha)
                .background(Color(0xFF25D366).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Reply,
                contentDescription = "Reply",
                tint = Color(0xFF25D366),
                modifier = Modifier.size(20.dp)
            )
        }

        Box(
            modifier = Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }
        ) {
            content()
        }
    }
}