package com.example.absapp.ui.screen

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.R
import com.example.common.R as common

sealed class BottomNavItem(
    val route: String,
    val label: Int,
    val icon: Int
) {
    object Chats : BottomNavItem("chats", common.string.msg, R.drawable.message_ic)
    object Calls : BottomNavItem("calls", common.string.call, R.drawable.call_ic)
    object Contacts : BottomNavItem("contacts", common.string.contact, R.drawable.user_ic)
    object Profile : BottomNavItem("profile", common.string.setting, R.drawable.settings_ic)
}