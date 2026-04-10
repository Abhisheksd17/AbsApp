package com.example.common.navigati.navigation

import com.example.common.navigati.navigation.Screen

// Basic operations
interface Navigation {
    suspend fun navigate(screen: Screen)
    suspend fun replace(screen: Screen)
    suspend fun pop()

    fun printStack()

    suspend fun popTo(screen: Screen)
    suspend fun popToIfExistsElseStart(screen: Screen)
    suspend fun refresh()

    fun current(): Screen?
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