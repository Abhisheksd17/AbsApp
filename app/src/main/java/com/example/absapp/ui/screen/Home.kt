package com.example.absapp.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.Screen
import com.example.feature_call.Call
import com.example.feature_chat.screens.Chat
import com.example.feature_contact.screen.Contacts
import com.example.feature_profile.screens.Profile


@Composable
fun Home(){

    val navController=rememberNavController()
    Scaffold(
        bottomBar = {
            HomeBottomBar(navController)
        }
    ) {padding ->
        NavHost(
            navController=navController,
            startDestination = Screen.Chats.route,
            modifier = Modifier.padding(padding)
        ){
            composable(Screen.Chats.route) { Chat() }
            composable(Screen.Calls.route) { Call() }
            composable(Screen.Contacts.route) { Contacts() }
            composable(Screen.Profile.route) { Profile() }
        }

    }
}