package com.example.common.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object OnBoarding : Screen("onboarding")
    object Otp : Screen("otp")
    object Update : Screen("update")
    object Chats : Screen("chats")
    object Calls : Screen("calls/{params}") {
        fun createRoute(params: String) =
            "calls/$params"
    }
    object Contacts : Screen("contacts")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Conversation : Screen("conversation/{userId}") {
        fun createRoute(userId: Int) = "conversation/$userId"
    }


}