package com.example.absapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.example.absapp.navigation.AppNavHost
import com.example.absapp.navigation.AppNavigator
import com.example.common.navigati.navigation.LocalNavigator
import com.example.absapp.ui.theme.AbsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CompositionLocalProvider(
                LocalNavigator provides navigator
            ) {
                AbsAppTheme {
                    AppNavHost(navigator)
                }
            }
        }
    }
}

