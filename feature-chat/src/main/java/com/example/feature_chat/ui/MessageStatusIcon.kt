package com.example.feature_chat.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.message.MessageStatus
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ReadBlue
import com.example.ui.theme.TimeColor

@Composable
fun MessageStatusIcon(
    status: MessageStatus,
    isSelf: Boolean,
    timeColor: Color
) {
    if (!isSelf) return
    when (status) {
        MessageStatus.SENDING -> CircularProgressIndicator(
            strokeWidth = 1.5.dp,
            modifier = Modifier.size(10.dp)
        )
        MessageStatus.SENT -> Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Sent",
            tint = TimeColor,
            modifier = Modifier.size(14.dp)
        )
        MessageStatus.DELIVERED -> DoubleCheckIcon(tint = TimeColor)
        MessageStatus.READ      -> DoubleCheckIcon(tint = ReadBlue)
        MessageStatus.FAILED    -> Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Failed",
            tint = ErrorRed,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun DoubleCheckIcon(tint: Color) {
    Box(modifier = Modifier.width(20.dp).height(14.dp)) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp).align(Alignment.CenterStart)
        )
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp).align(Alignment.CenterEnd)
        )
    }
}