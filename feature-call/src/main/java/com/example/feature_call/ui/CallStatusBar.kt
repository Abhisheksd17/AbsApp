package com.example.feature_call.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ConnectingText
import com.example.ui.theme.StatusBarBackground
import com.example.ui.theme.StatusBarTextPrimary
import com.example.ui.theme.StatusBarTextSecondary

@Composable
 fun CallStatusBar(
    peerName: String,
    duration: String,
    isConnected: Boolean
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = StatusBarBackground
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = peerName,
                color = StatusBarTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (duration.isNotEmpty()) {
                Text(
                    text = duration,
                    color = StatusBarTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            if (!isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    PulsingDot()
                    Text(
                        text = "Connecting...",
                        color = ConnectingText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}