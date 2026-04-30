package com.example.absapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.common.navigation.Screen
import com.example.feature_call.Call
import com.example.feature_chat.screens.Chat
import com.example.feature_chat.screens.Conversation
import com.example.feature_contact.screen.Contacts
import com.example.feature_profile.screens.Profile


@Composable
fun Home() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = shouldShowBottomBar(currentRoute)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                HomeBottomBar(navController)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Chats.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Chats.route) { Chat() }
            composable(Screen.Calls.route) { Call() }
            composable(Screen.Profile.route) { Profile() }

            composable(Screen.Contacts.route) {
                Contacts(
                    onOpenConversation = { userId ->
                        navController.navigate(
                            Screen.Conversation.createRoute(userId)
                        )
                    }
                )
            }

            composable(
                route = Screen.Conversation.route,
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val userId = backStackEntry.arguments?.getInt("userId")
                Conversation(userId = userId)
            }
        }
    }
}

val hiddenRoutes = listOf("conversation")

fun shouldShowBottomBar(route: String?): Boolean {
    return route?.let {
        hiddenRoutes.none { hidden -> it.startsWith(hidden) }
    } ?: true
}