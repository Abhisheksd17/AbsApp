package com.example.absapp.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.absapp.ui.components.IncomingCallOverlay
import com.example.common.navigation.Screen
import com.example.common.util.NotificationDestination
import com.example.common.viewmodel.CallerViewModel
import com.example.common.viewmodel.NotificationViewModel
import com.example.feature_call.screens.Call
import com.example.feature_chat.screens.Chat
import com.example.feature_chat.screens.Conversation
import com.example.feature_contact.screen.Contacts
import com.example.feature_profile.screens.Profile
import com.example.model.call.CallParams


@Composable
fun Home() {

    val navController = rememberNavController()
    val callViewModel: CallerViewModel = hiltViewModel()
    val callState by callViewModel.callState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var currentCallParams by remember {
        mutableStateOf<CallParams?>(null)
    }

    val notifViewModel: NotificationViewModel = hiltViewModel()


    val showBottomBar = shouldShowBottomBar(currentRoute)


    LaunchedEffect(Unit) {
        notifViewModel.destination.collect { dest ->
            when (dest) {
                is NotificationDestination.OpenConversation ->
                    navController.navigate(
                        Screen.Conversation.createRoute(dest.userId)
                    )

                is NotificationDestination.OpenCall ->{

                    currentCallParams = CallParams.fromRoute(dest.params)
                    navController.navigate(Screen.Calls.createRoute(dest.params))
                }


                null -> Unit
            }
        }
    }

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
        )
        {

            composable(Screen.Chats.route) {
                Chat(
                    onOpenConversation = { userId ->
                        navController.navigate(
                            Screen.Conversation.createRoute(userId)
                        )
                    }
                )
            }

            composable(
                route = Screen.Calls.route,
                arguments = listOf(
                    navArgument("params") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val paramsArg = backStackEntry.arguments?.getString("params")

                val resolvedParams = currentCallParams
                    ?: paramsArg?.let { CallParams.fromRoute(it) }

                resolvedParams?.let { params ->
                    Call(
                        params = params,
                        onCallEnded = {
                            navController.popBackStack()
                            currentCallParams = null
                            callViewModel.resetState()
                        }
                    )
                }
            }

            composable(Screen.Profile.route) {
                Profile()
            }

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
                Conversation(
                    userId = userId,
                    onNavigateToCall = { params ->
                        currentCallParams = params
                        navController.navigate(Screen.Calls.createRoute("active"))
                    }
                )
            }
        }

        IncomingCallOverlay(
            callState = callState,
            viewModel = callViewModel,
            onNavigateToCall = { activeState ->
                currentCallParams = activeState.params
                navController.navigate(Screen.Calls.route)
            }
        )
    }
}

val hiddenRoutes = listOf("conversation","calls")

fun shouldShowBottomBar(route: String?): Boolean {
    return route?.let {
        hiddenRoutes.none { hidden -> it.startsWith(hidden) }
    } ?: true
}