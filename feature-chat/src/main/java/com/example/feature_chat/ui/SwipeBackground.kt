package com.example.feature_chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ui.R
import com.example.ui.theme.Black
import com.example.ui.theme.ErrorRed

@Composable
fun SwipeBackground(state: SwipeToDismissBoxState) {

    val color = when (state.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Black// Archive (green)
        SwipeToDismissBoxValue.EndToStart -> ErrorRed // Delete (red)
        else -> Color.Transparent
    }

    val alignment = when (state.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val icon = when (state.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> R.drawable.delete_ic
        SwipeToDismissBoxValue.EndToStart -> R.drawable.delete_ic
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}