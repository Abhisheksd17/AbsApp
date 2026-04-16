package com.example.absapp.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.Screen
import com.example.ui.screen.HomeBottomNav

@Composable
fun Home(){

    val navController=rememberNavController()
    Scaffold(
        bottomBar = HomeBottomNav(navController)
    ) {padding ->
        NavHost(
            navController=navController,
            startDestination = "chats",
            modifier = Modifier.padding(padding)
        ){
            composable(Screen.Chats.route) {  }
        }

    }
}