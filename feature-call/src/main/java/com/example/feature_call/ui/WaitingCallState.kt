package com.example.feature_call.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AnimatedCircleInner
import com.example.ui.theme.AnimatedCircleOuter
import com.example.ui.theme.AvatarBackground
import com.example.ui.theme.AvatarText
import com.example.ui.theme.PeerNameText
import com.example.ui.theme.WaitingStateText

@Composable
 fun WaitingCallState(
    peerName: String,
    isCaller: Boolean,
    isConnected: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_pulse"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Animated background circles
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(scale)
                .background(
                    AnimatedCircleOuter,
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(scale * 0.9f)
                .background(
                    AnimatedCircleInner,
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar placeholder
            Surface(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                color = AvatarBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = peerName.firstOrNull()?.uppercase() ?: "U",
                        color = AvatarText,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = peerName,
                color = PeerNameText,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isConnected) {
                    PulsingDot()
                }
                Text(
                    text = when {
                        !isConnected && isCaller -> "Ringing..."
                        !isConnected -> "Connecting..."
                        else -> "Connected"
                    },
                    color = WaitingStateText,
                    fontSize = 16.sp
                )
            }
        }
    }
}