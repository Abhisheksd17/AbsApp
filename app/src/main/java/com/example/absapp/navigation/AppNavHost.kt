package com.example.absapp.navigation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.absapp.ui.components.IncomingCallOverlay
import com.example.absapp.ui.screen.HomeBottomBar
import com.example.absapp.ui.screen.Splash
import com.example.common.navigation.NavAction
import com.example.common.navigation.Screen
import com.example.common.util.NotificationDestination
import com.example.common.viewmodel.CallerViewModel
import com.example.common.viewmodel.NotificationViewModel
import com.example.feature_auth.ui.OnBoarding
import com.example.feature_auth.ui.OtpVerification
import com.example.feature_auth.ui.SignIn
import com.example.feature_auth.ui.SignUp
import com.example.feature_auth.ui.UpdateProfile
import com.example.feature_call.screens.Call
import com.example.feature_chat.screens.Chat
import com.example.feature_chat.screens.Conversation
import com.example.feature_contact.screen.Contacts
import com.example.feature_profile.screens.Profile
import com.example.model.call.CallParams

@Composable
fun AppNavHost(navigator: AppNavigator) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val notifViewModel: NotificationViewModel = hiltViewModel(context as ComponentActivity)
    val destination by notifViewModel.destination.collectAsStateWithLifecycle(null)


    val callViewModel: CallerViewModel = hiltViewModel()
    val callState by callViewModel.callState.collectAsStateWithLifecycle()
    var currentCallParams by remember { mutableStateOf<CallParams?>(null) }

    val isBottomBarVisible = when {
        currentRoute == null -> false
        currentRoute == Screen.Splash.route -> false
        currentRoute == Screen.SignIn.route -> false
        currentRoute == Screen.SignUp.route -> false
        currentRoute == Screen.OnBoarding.route -> false
        currentRoute == Screen.Otp.route -> false
        currentRoute == Screen.Update.route -> false
        currentRoute.startsWith("conversation") -> false
        currentRoute.startsWith("calls/") && currentRoute != "calls/all" -> false
        else -> true
    }

    LaunchedEffect(destination) {
        destination?.let { dest ->
            when (dest) {

                is NotificationDestination.OpenConversation -> {
                    navController.navigate(Screen.Conversation.createRoute(dest.userId)) {

                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                is NotificationDestination.OpenCall -> {
                    currentCallParams = CallParams.fromRoute(dest.params)
                    navController.navigate(Screen.Calls.createRoute(dest.params)) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
            notifViewModel.clear()
        }
    }

    LaunchedEffect(Unit) {
        navigator.events.collect { action ->
            when (action) {
                is NavAction.Navigate -> {
                    val route =  action.route
                    navController.navigate(route)
                }
                is NavAction.Replace -> {
                    val route =  action.route
                    navController.navigate(route) {
                        popUpTo(navController.currentDestination?.route ?: "") { inclusive = true }
                    }
                }
                is NavAction.Pop -> navController.popBackStack()
                is NavAction.PopTo -> navController.popBackStack(action.route, false)
                is NavAction.ClearAndNavigate -> {
                    val route = action.route
                    navController.navigate(route) { popUpTo(0) }
                }
                is NavAction.Refresh -> {
                    navController.navigate(action.route) {
                        popUpTo(action.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                HomeBottomBar(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
            // Auth Flow
            composable(Screen.Splash.route) { Splash() }
            composable(Screen.OnBoarding.route) { OnBoarding() }
            composable(Screen.SignIn.route) { SignIn() }
            composable(Screen.SignUp.route) { SignUp() }
            composable(Screen.Otp.route) { OtpVerification() }
            composable(Screen.Update.route) { UpdateProfile() }


            composable(Screen.Chats.route) {
                Chat(onOpenConversation = { userId ->
                    navController.navigate(Screen.Conversation.createRoute(userId))
                })
            }

            composable(
                route = Screen.Calls.route,
                arguments = listOf(navArgument("params") { type = NavType.StringType; defaultValue = "all" })
            ) { backStackEntry ->
                val paramsArg = backStackEntry.arguments?.getString("params")
                val resolvedParams = currentCallParams ?: paramsArg?.let { CallParams.fromRoute(it) }
                resolvedParams?.let { params ->
                    Call(params = params, onCallEnded = {
                        navController.popBackStack()
                        currentCallParams = null
                        callViewModel.resetState()
                    })
                }
            }
            
            composable("calls") {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Calls.createRoute("all")) {
                        popUpTo("calls") { inclusive = true }
                    }
                }
            }

            composable(Screen.Profile.route) { Profile() }

            composable(Screen.Contacts.route) {
                Contacts(onOpenConversation = { userId ->
                    navController.navigate(Screen.Conversation.createRoute(userId))
                })
            }

            composable(
                route = Screen.Conversation.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                Conversation(userId = userId, onNavigateToCall = { params ->
                    currentCallParams = params
                    navController.navigate(Screen.Calls.createRoute("active"))
                })
            }
        }

        IncomingCallOverlay(
            callState = callState,
            viewModel = callViewModel,
            onNavigateToCall = { activeState ->
                currentCallParams = activeState.params
                navController.navigate(Screen.Calls.createRoute("active"))
            }
        )
    }
}
