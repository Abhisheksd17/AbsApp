package com.example.feature_chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.R
import com.example.common.R as common
import com.example.common.util.Utils.formatTime
import com.example.ui.R as ui
import com.example.ui.theme.Black
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TealGreen
import com.example.ui.theme.White
import com.example.ui.theme.carosBold
import com.example.ui.theme.carosLight
import com.example.ui.theme.carosMedium
import com.example.ui.theme.circularStd

@Composable
fun AiChat(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .clickable { onClick() }
            .padding(bottom = 10.dp)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = ui.drawable.ai_icon),
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = stringResource(common.string.absbot),
                    fontFamily = carosMedium,
                    color = Black,
                    fontSize = 15.sp
                )

                Text(
                    text = stringResource(common.string.bot_desc),
                    fontSize = 10.sp,
                    color = SlateGray,
                    fontFamily = circularStd
                )
            }
        }
    }
}