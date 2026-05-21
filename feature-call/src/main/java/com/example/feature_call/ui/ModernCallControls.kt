package com.example.feature_call.ui

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
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ControlBarBackground
import com.example.ui.theme.HangUpButton
import com.example.ui.theme.HangUpIcon

@Composable
 fun ModernCallControls(
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    isCameraOn: Boolean,
    isVideo: Boolean,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleCamera: () -> Unit,
    onSwitchCamera: () -> Unit,
    onHangUp: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(32.dp),
        color = ControlBarBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top row controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute button
                ControlButton(
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (isMuted) "Unmute" else "Mute",
                    isActive = isMuted,
                    onClick = onToggleMute
                )

                // Speaker button
                ControlButton(
                    icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                    label = "Speaker",
                    isActive = !isSpeakerOn,
                    onClick = onToggleSpeaker
                )

                if (isVideo) {
                    ControlButton(
                        icon = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        label = "Camera",
                        isActive = !isCameraOn,
                        onClick = onToggleCamera
                    )

                    ControlButton(
                        icon = Icons.Default.Cameraswitch,
                        label = "Flip",
                        isActive = false,
                        onClick = onSwitchCamera
                    )
                }
            }

            Surface(
                onClick = onHangUp,
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = HangUpButton
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = HangUpIcon,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}