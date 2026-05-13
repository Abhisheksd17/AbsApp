package com.example.feature_call.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.VideocamOff
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Black
import com.example.ui.theme.White

@Composable
 fun CallControls(
    isMuted:         Boolean,
    isSpeakerOn:     Boolean,
    isCameraOn:      Boolean,
    isVideo:         Boolean,
    callDuration:    String,
    peerName:        String,
    onToggleMute:    () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleCamera:  () -> Unit,
    onSwitchCamera:  () -> Unit,
    onHangUp:        () -> Unit,
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .background(
                Black.copy(alpha = 0.55f),
                RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
     Text(peerName,      color = White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        if (callDuration.isNotEmpty()) {
            Text(callDuration, color = White.copy(alpha = 0.6f), fontSize = 14.sp)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier              = Modifier.fillMaxWidth()
        ) {
            CallButton(
                icon    = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                label   = if (isMuted) "Unmute" else "Mute",
                tint    = if (isMuted) Color(0xFFFF5252) else White,
                onClick = onToggleMute
            )

            if (isVideo) {
                CallButton(
                    icon    = if (isCameraOn) Icons.Rounded.Videocam else Icons.Rounded.VideocamOff,
                    label   = if (isCameraOn) "Camera" else "No cam",
                    tint    = if (!isCameraOn) Color(0xFFFF5252) else White,
                    onClick = onToggleCamera
                )
                CallButton(
                    icon    = Icons.Rounded.FlipCameraAndroid,
                    label   = "Flip",
                    tint    = White,
                    onClick = onSwitchCamera
                )
            }

            CallButton(
                icon    = if (isSpeakerOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                label   = "Speaker",
                tint    = if (!isSpeakerOn) Color(0xFFFF5252) else Color.White,
                onClick = onToggleSpeaker
            )

            Box(
                modifier            = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
                    .clickable(onClick = onHangUp),
                contentAlignment    = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Rounded.CallEnd,
                    contentDescription = "End call",
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp)
                )
            }
        }
    }
}