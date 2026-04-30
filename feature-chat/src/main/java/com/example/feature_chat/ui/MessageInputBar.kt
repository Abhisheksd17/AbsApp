package com.example.feature_chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LightGrayBackground
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftLightGray
import com.example.ui.R as ui

@Composable
fun MessageInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onAttachClick: () -> Unit = {},
    onEmojiClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onMicClick: () -> Unit = {},
    onSend: () -> Unit = {},
    modifier: Modifier = Modifier
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBackground)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {

        Icon(
            painter = painterResource(id = ui.drawable.attach_ic),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onAttachClick() },
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(12.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(SoftLightGray)
                .padding(vertical = 14.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {

            BasicTextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = SlateGray
                ),
                decorationBox = { innerTextField ->
                    if (message.isEmpty()) {
                        Text(
                            text = "Write your message",
                            color = SoftLightGray,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )

            Icon(
                painter = painterResource(id = ui.drawable.smiley_ic),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onEmojiClick() },
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (message.isNotBlank()) {
            Icon(
                painter =painterResource(id=ui.drawable.send_ic),
                contentDescription = "Send",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onSend() },
                tint = Color.Unspecified
            )
        } else {
            Icon(
                painter = painterResource(id = ui.drawable.camera_ic),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCameraClick() },
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                painter = painterResource(id = ui.drawable.vr_ic),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onMicClick() },
                tint = Color.Unspecified
            )
        }
    }
}