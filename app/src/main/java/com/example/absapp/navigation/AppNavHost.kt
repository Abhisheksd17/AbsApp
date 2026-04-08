package com.example.absapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.absapp.ui.screen.Home
import com.example.absapp.ui.screen.Splash
import com.example.feature_auth.ui.OnBoarding
import com.example.feature_auth.ui.SignIn
import com.example.feature_auth.ui.SignUp

@Composable
fun AppNavHost(navigator: AppNavigator) {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        navigator.events.collect { action ->

            when (action) {

                is NavAction.Navigate -> {
                    navController.navigate(action.route)
                }

                is NavAction.Replace -> {
                    navController.navigate(action.route) {
                        popUpTo(navController.currentDestination?.route ?: "") {
                            inclusive = true
                        }
                    }
                }

                is NavAction.Pop -> {
                    navController.popBackStack()
                }

                is NavAction.PopTo -> {
                    navController.popBackStack(action.route, false)
                }

                is NavAction.ClearAndNavigate -> {
                    navController.navigate(action.route) {
                        popUpTo(0)
                    }
                }

                is NavAction.Refresh -> {
                    navController.navigate(action.route) {
                        popUpTo(action.route) { inclusive = true }
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) { Splash() }
        composable(Screen.OnBoarding.route) { OnBoarding() }
        composable(Screen.SignIn.route) { SignIn() }
        composable(Screen.SignUp.route) { SignUp() }
        composable(Screen.Home.route) { Home() }
    }



}