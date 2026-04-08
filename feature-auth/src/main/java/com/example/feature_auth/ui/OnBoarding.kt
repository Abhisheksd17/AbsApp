package com.example.feature_auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnBoarding(){


    Box(
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
            .background(
                color = onBoardingGradient
            )
    )
}