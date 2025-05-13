package com.buildmasterapp.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.vector.ImageVector
import com.buildmasterapp.R


sealed class Screen(val route: String, val titleResId: Int, val icon: ImageVector? = null) {
    object Home : Screen("home", R.string.home, Icons.Filled.Home)
    object PCConfig : Screen("pc_config", R.string.pc_config, Icons.Filled.Computer)
    object Chat : Screen("chat", R.string.chat, Icons.Filled.Chat)
    object Store : Screen("store", R.string.store, Icons.Filled.Store)
    object Prices : Screen("prices", R.string.prices, Icons.Filled.AttachMoney)

    // Para el Navigation Drawer (sin íconos en la definición de ruta principal)
    object Profile : Screen("profile", R.string.profile)
    object Settings : Screen("settings", R.string.settings)
    object Language : Screen("language", R.string.language)
    object Support : Screen("support", R.string.support)
    object Logout : Screen("logout", R.string.logout)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.PCConfig,
    Screen.Chat,
    Screen.Store,
    Screen.Prices

)

val drawerNavItems = listOf(
    Screen.Profile,
    Screen.Settings,
    Screen.Language,
    Screen.Support,
    Screen.Logout
)