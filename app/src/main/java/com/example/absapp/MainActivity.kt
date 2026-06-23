package com.example.absapp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import com.example.absapp.navigation.AppNavHost
import com.example.absapp.navigation.AppNavigator
import com.example.absapp.ui.theme.AbsAppTheme
import com.example.common.navigati.navigation.LocalNavigator
import com.example.common.util.NotificationDestination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.common.viewmodel.NotificationViewModel
import com.example.feature_notifications.NotificationConstants.EXTRA_CHAT_ID


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val notifViewModel: NotificationViewModel by viewModels()
    @Inject
    lateinit var navigator: AppNavigator

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Handle notification if app was started from one (Cold Start)
        handleNotificationIntent(intent)

        setContent {
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            CompositionLocalProvider(
                LocalNavigator provides navigator
            ) {
                AbsAppTheme {
                    AppNavHost(navigator)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return

        Log.d("NOTIFICATION_DEBUG", "Processing Intent: $intent")

        intent.extras?.keySet()?.forEach { key ->
            Log.d("NOTIFICATION_DEBUG", "Extra: $key = ${intent.extras?.get(key)}")
        }

        val type = intent.getStringExtra("type")
        Log.d("NOTIFICATION_DEBUG", "type = $type")

        when(type) {
            "chat" -> {
                val chatIdString = intent.getStringExtra(EXTRA_CHAT_ID)
                Log.d("NOTIFICATION_DEBUG", "chatIdString = $chatIdString")
                val chatId = chatIdString?.toIntOrNull()
                if (chatId != null && chatId != -1) {
                    notifViewModel.onNotification(
                        NotificationDestination.OpenConversation(chatId)
                    )
                }
            }

            "call" -> {
                val params = intent.getStringExtra("params")
                Log.d("NOTIFICATION_DEBUG", "params = $params")
                if (params != null) {
                    notifViewModel.onNotification(
                        NotificationDestination.OpenCall(params)
                    )
                }
            }
        }
    }
}
