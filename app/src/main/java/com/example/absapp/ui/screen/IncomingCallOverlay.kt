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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * Root-level overlay that displays incoming call UI.
 *
 * Place this in Home() composable, listening to viewModel.callState.
 * When CallState.Incoming, this overlay appears on top of all navigation.
 */
@Composable
fun IncomingCallOverlay(
    callState: CallState,
    viewModel: CallerViewModel,
    onNavigateToCall: (CallState.Active) -> Unit,
) {
    AnimatedVisibility(
        visible  = callState is CallState.Incoming,
        enter    = fadeIn(),
        exit     = fadeOut(),
    ) {
        val event = (callState as? CallState.Incoming)?.event ?: return@AnimatedVisibility

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A0A1A), Color(0xFF1A1035))
                    )
                )
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {

                PulsingAvatar(name = event.callerName)

                Spacer(Modifier.height(28.dp))

                Text(
                    text       = event.callerName,
                    color      = Color.White,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = if (event.callType == "video") "Incoming video call" else "Incoming voice call",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                )

                Spacer(Modifier.height(64.dp))

                // Accept / Decline buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(80.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    // Decline
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingActionButton(
                            onClick = {
                                viewModel.rejectCall(event)
                            },
                            containerColor = Color(0xFFFF3B30),
                            contentColor   = Color.White,
                            modifier       = Modifier.size(68.dp),
                            shape          = CircleShape,
                        ) {
                            Icon(
                                Icons.Rounded.CallEnd,
                                contentDescription = "Decline",
                                modifier = Modifier.size(30.dp),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Decline", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    }

                    // Accept
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingActionButton(
                            onClick = {
                                viewModel.acceptCall(event)
                            },
                            containerColor = Color(0xFF34C759),
                            contentColor   = Color.White,
                            modifier       = Modifier.size(68.dp),
                            shape          = CircleShape,
                        ) {
                            Icon(
                                Icons.Rounded.Call,
                                contentDescription = "Accept",
                                modifier = Modifier.size(30.dp),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Accept", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (callState is CallState.Active) {
        onNavigateToCall(callState)
    }
}

@Composable
private fun PulsingAvatar(name: String) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue   = 1f,
        targetValue    = 1.15f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    Box(
        modifier = Modifier
            .size(130.dp)
            .scale(pulseScale)
            .border(3.dp, Color(0xFF7C4DFF).copy(alpha = 0.5f), CircleShape)
            .background(Color(0xFF3D2B80), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = name.take(1).uppercase(),
            color      = Color.White,
            fontSize   = 52.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}