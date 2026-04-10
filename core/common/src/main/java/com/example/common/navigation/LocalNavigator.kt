package com.example.common.navigati.navigation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigator = staticCompositionLocalOf<Navigation> {
    error("Navigator not provided")
}