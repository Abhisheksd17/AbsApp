package com.example.feature_call.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ControlButtonActive
import com.example.ui.theme.ControlButtonIconActive
import com.example.ui.theme.ControlButtonIconInactive
import com.example.ui.theme.ControlButtonInactive
import com.example.ui.theme.ControlButtonLabel

@Composable
 fun ControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = if (isActive) ControlButtonActive else ControlButtonInactive
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) ControlButtonIconActive else ControlButtonIconInactive,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = label,
            color = ControlButtonLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}