package com.example.absapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.viewmodel.CallerViewModel
import com.example.domain.data.CallState
import com.example.ui.theme.AcceptButtonColor
import com.example.ui.theme.AvatarBackground
import com.example.ui.theme.AvatarInnerRing
import com.example.ui.theme.AvatarMiddleRing
import com.example.ui.theme.AvatarOuterRing
import com.example.ui.theme.DeclineButtonColor
import com.example.ui.theme.GradientEnd
import com.example.ui.theme.GradientStart


@Composable
fun IncomingCallOverlay(
    callState: CallState,
    viewModel: CallerViewModel,
    onNavigateToCall: (CallState.Active) -> Unit,
) {

    LaunchedEffect(callState) {
        if (callState is CallState.Active) {
            onNavigateToCall(callState)
        }
    }

    val incomingEvent = (callState as? CallState.Incoming)?.event

    AnimatedVisibility(
        visible = callState is CallState.Incoming,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        val event = incomingEvent ?: return@AnimatedVisibility

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {

                EnhancedPulsingAvatar(name = event.callerName)

                Spacer(Modifier.height(32.dp))

                Text(
                    text = event.callerName,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (event.callType == "video") Icons.Rounded.Videocam else Icons.Rounded.Call,
                        contentDescription = null,
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (event.callType == "video") "Incoming video call" else "Incoming voice call",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(80.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(100.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CallActionButton(
                        onClick = { viewModel.rejectCall(event) },
                        backgroundColor = DeclineButtonColor,
                        icon = Icons.Rounded.CallEnd,
                        label = "Decline",
                        iconSize = 32.dp
                    )

                    CallActionButton(
                        onClick = { viewModel.acceptCall(event) },
                        backgroundColor = AcceptButtonColor,
                        icon = Icons.Rounded.Call,
                        label = "Accept",
                        iconSize = 32.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedPulsingAvatar(name: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val outerScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "outerScale",
    )

    val middleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "middleScale",
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(outerScale)
                .background(AvatarOuterRing, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(middleScale)
                .background(AvatarMiddleRing, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(170.dp)
                .background(AvatarInnerRing, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(140.dp)
                .background(AvatarBackground, CircleShape)
                .border(3.dp, Color(0xFF6C5CE7).copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "U",
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun CallActionButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconSize: androidx.compose.ui.unit.Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = backgroundColor,
            contentColor = Color.White,
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(iconSize),
            )
        }

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}