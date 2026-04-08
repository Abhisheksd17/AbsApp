package com.example.absapp.navigation

sealed class NavAction {
    data class Navigate(val route: String) : NavAction()
    data class Replace(val route: String) : NavAction()
    object Pop : NavAction()
    data class PopTo(val route: String) : NavAction()
    data class ClearAndNavigate(val route: String) : NavAction()
    data class Refresh(val route: String) : NavAction()
}