package com.buildmasterapp.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.buildmasterapp.R

sealed class Screen(val route: String, val titleResId: Int, val icon: ImageVector? = null) {
    object Home : Screen("home", R.string.home, Icons.Filled.Home)
    object PCConfig : Screen("pc_config", R.string.pc_config, Icons.Filled.Computer)
    object Chat : Screen("chat", R.string.chat, Icons.Filled.Chat)

    object Prices : Screen("prices", R.string.prices, Icons.Filled.AttachMoney)

    object Catalogue : Screen("catalogue", R.string.catalogue)

    // Drawer
    object Profile : Screen("profile", R.string.profile)
    object Settings : Screen("settings", R.string.settings)

    object Glossary : Screen("glossary", R.string.glossary)
    object Logout : Screen("logout", R.string.logout)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.PCConfig,
    Screen.Chat,
    Screen.Prices // Eliminado Store
)

val drawerNavItems = listOf(
    Screen.Profile,
    Screen.Settings,

    Screen.Glossary,
    Screen.Logout
)