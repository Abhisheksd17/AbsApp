package com.example.absapp.navigation

// Basic operations
interface Navigation {
    suspend fun navigate(screen: Screen)
    suspend fun replace(screen: Screen)
    suspend fun pop()
}

// Advanced operations
interface IAdvancedNavigation {
    suspend fun popTo(screen: Screen)
    suspend fun popToIfExistsElseStart(screen: Screen)
    suspend fun refresh()
}

// Debugging
interface INavigationDebug {
    fun printStack()
    fun current(): Screen?
}