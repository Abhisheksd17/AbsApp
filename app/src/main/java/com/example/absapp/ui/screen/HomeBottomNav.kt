package com.example.absapp.ui.screen

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TealGreen

@Composable
fun HomeBottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Chats,
        BottomNavItem.Calls,
        BottomNavItem.Contacts,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        items.forEach { item ->

            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,

                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null
                    )
                },

                label = {
                    Text(stringResource(id = item.label))
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealGreen,
                    selectedTextColor = TealGreen,
                    unselectedIconColor = SlateGray,
                    unselectedTextColor = SlateGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}