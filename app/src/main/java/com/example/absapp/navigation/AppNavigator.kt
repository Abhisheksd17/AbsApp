package com.example.absapp.navigation

import com.example.common.navigati.navigation.IAdvancedNavigation
import com.example.common.navigati.navigation.INavigationDebug
import com.example.common.navigation.NavAction
import com.example.common.navigati.navigation.Navigation
import com.example.common.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNavigator @Inject constructor() :
    Navigation, IAdvancedNavigation, INavigationDebug {

    private val _events = MutableSharedFlow<NavAction>()
    val events = _events.asSharedFlow()

    private val stack = mutableListOf<Screen>()
    override suspend fun navigate(screen: Screen) {
        stack.add(screen)
        _events.emit(NavAction.Navigate(screen.route))
    }

    override suspend fun replace(screen: Screen) {
        if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex)
        stack.add(screen)
        _events.emit(NavAction.Replace(screen.route))
    }

    override suspend fun pop() {
        if (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex)
            _events.emit(NavAction.Pop)
        }
    }

    override suspend fun popTo(screen: Screen) {
        if (stack.contains(screen)) {
            while (stack.last() != screen) stack.removeAt(stack.lastIndex)
            _events.emit(NavAction.PopTo(screen.route))
        }
    }

    override suspend fun popToIfExistsElseStart(screen: Screen) {
        if (stack.contains(screen)) {
            popTo(screen)
        } else {
            stack.clear()
            stack.add(screen)
            _events.emit(NavAction.ClearAndNavigate(screen.route))
        }
    }

    override suspend fun refresh() {
        stack.lastOrNull()?.let {
            _events.emit(NavAction.Refresh(it.route))
        }
    }


    override fun printStack() {
        println("----- NAV STACK -----")
        stack.forEachIndexed { index, screen ->
            println("$index -> ${screen.route}")
        }
        println("---------------------")
    }

    override fun current(): Screen? = stack.lastOrNull()
}