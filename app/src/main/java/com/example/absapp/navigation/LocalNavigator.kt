package com.example.absapp.navigation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigator = staticCompositionLocalOf<AppNavigator> {
    error("Navigator not provided")
}