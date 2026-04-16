package com.example.common.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object OnBoarding : Screen("onboarding")
    object Home : Screen("home")
    object Otp : Screen("otp")
    object Update : Screen("update")
    object Chats : Screen("chats")
    object Calls : Screen("calls")
    object Contacts : Screen("contacts")
    object Settings : Screen("settings")

}