package com.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoolSlateGray
import com.example.ui.theme.carosMedium

object Button {


    @Composable
    fun SocialIconButton(
        iconRes: Int,
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { onClick() }
                .border(1.dp,CoolSlateGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    @Composable
    fun SubmitButton(
        text: String,
        onClick: () -> Unit,
        isSelected: Boolean,
        modifier: Modifier = Modifier,
        borderColor: Color,
        backgroundColor: Color,
        selectedBackgroundColor: Color,
        textColor: Color,
        selectedTextColor: Color
    ) {
        val bgColor = if (isSelected) selectedBackgroundColor else backgroundColor
        val shape = RoundedCornerShape(16.dp)
        val txtColor = if (isSelected) selectedTextColor else textColor

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(bgColor, shape)
                .border(1.dp, borderColor, shape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                fontSize = 16.sp,
                text = text,
                color = txtColor,
                fontFamily = carosMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}