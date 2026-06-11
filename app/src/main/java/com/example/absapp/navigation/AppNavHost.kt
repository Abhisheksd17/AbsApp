package com.example.absapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.absapp.ui.screen.Home
import com.example.absapp.ui.screen.Splash
import com.example.common.navigation.NavAction
import com.example.common.navigation.Screen
import com.example.common.viewmodel.NotificationViewModel
import com.example.feature_auth.ui.OnBoarding
import com.example.feature_auth.ui.OtpVerification
import com.example.feature_auth.ui.SignIn
import com.example.feature_auth.ui.SignUp
import com.example.feature_auth.ui.UpdateProfile

@Composable
fun AppNavHost(navigator: AppNavigator) {

    val notifViewModel: NotificationViewModel = hiltViewModel()
    val destination by notifViewModel.destination.collectAsStateWithLifecycle(null)
    val navController = rememberNavController()


    LaunchedEffect(destination) {
        if (destination != null &&
            navController.currentDestination?.route != Screen.Home.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
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
        composable(Screen.Otp.route) { OtpVerification() }
        composable(Screen.Update.route) { UpdateProfile() }
        composable(Screen.Home.route) { Home() }
    }


}