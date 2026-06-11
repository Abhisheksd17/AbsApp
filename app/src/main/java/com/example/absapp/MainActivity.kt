package com.example.absapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.absapp.navigation.AppNavHost
import com.example.absapp.navigation.AppNavigator
import com.example.common.navigati.navigation.LocalNavigator
import com.example.absapp.ui.theme.AbsAppTheme
import com.example.common.navigation.Screen
import com.example.common.util.NotificationDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.common.viewmodel.NotificationViewModel
import com.example.feature_notifications.NotificationConstants.EXTRA_CHAT_ID


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val notifViewModel: NotificationViewModel by viewModels()
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
       when(intent?.getStringExtra("type")){
           "chat" -> {
               val userId = intent.getStringExtra(EXTRA_CHAT_ID)?.toIntOrNull() ?: return
               if (userId != -1)
                   notifViewModel.onNotification(
                       NotificationDestination.OpenConversation(userId)
                   )
           }
           "call" -> {
               val params = intent.getStringExtra("params") ?: return
               notifViewModel.onNotification(
                   NotificationDestination.OpenCall(params)
               )
           }
       }

        }
}

