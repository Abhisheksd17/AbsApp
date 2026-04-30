package com.example.feature_chat.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The small quoted block rendered *inside* a bubble when the message
 * is a reply to another message.
 */
@Composable
fun ReplyQuoteChip(
    originalMessage: ChatMessage,
    currentUserId: Int,
    isSelfBubble: Boolean,
    modifier: Modifier = Modifier
) {
    val isOriginalSelf = originalMessage.senderId == currentUserId
    val accentColor    = if (isOriginalSelf) Color(0xFF25D366) else Color(0xFF128C7E)
    val chipBg         = if (isSelfBubble)
        Color(0xFFDCF8C6).copy(alpha = 0.6f)   // inside outgoing bubble
    else
        Color(0xFFEEEEEE)                       // inside incoming bubble

    Row(
        modifier = modifier
            .widthIn(max = 260.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(chipBg)
            .padding(start = 0.dp)
    ) {
        // Accent left bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .background(accentColor)
        )

        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text       = if (isOriginalSelf) "You" else originalMessage.senderName,
                color      = accentColor,
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text     = when {
                    originalMessage.isVoice    -> "\uD83C\uDFA4 Voice message"
                    originalMessage.imageUrl != null -> "\uD83D\uDCF7 Photo"
                    else -> originalMessage.text
                },
                color    = Color(0xFF555555),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}