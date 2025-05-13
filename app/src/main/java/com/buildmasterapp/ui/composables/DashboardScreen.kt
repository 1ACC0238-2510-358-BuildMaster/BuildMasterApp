package com.buildmasterapp.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import coil3.compose.rememberAsyncImagePainter
import com.buildmasterapp.Navigation.AppNavHost
import com.buildmasterapp.Navigation.Screen
import com.buildmasterapp.Navigation.bottomNavItems
import com.buildmasterapp.Navigation.drawerNavItems
import com.buildmasterapp.ui.theme.BuildMasterTheme
import kotlinx.coroutines.launch
import com.buildmasterapp.R


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determinar el título actual basado en la ruta
    val currentScreen = bottomNavItems.find { it.route == currentDestination?.route }
        ?: drawerNavItems.find { it.route == currentDestination?.route }
        ?: Screen.Home // Default o pantalla de error

    val topBarTitle = if (currentScreen != Screen.Home && currentDestination?.route != Screen.Home.route && bottomNavItems.any { it.route == currentDestination?.route }) {
        stringResource(id = currentScreen.titleResId)
    } else {
        stringResource(R.string.app_name) // Título principal para Home y vistas del Drawer
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Aquí puedes agregar un encabezado al Drawer si lo deseas
                // Spacer(Modifier.height(12.dp))
                drawerNavItems.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = stringResource(id = screen.titleResId)) } },
                        label = { Text(stringResource(id = screen.titleResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter("https://i.postimg.cc/KjnzrSC9/Build-Master-Logo-negro-sin-fongo.jpg"),
                                contentDescription = stringResource(R.string.app_logo),
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(topBarTitle)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isOpen) drawerState.close() else drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.menu)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screen.Profile.route) {
                                launchSingleTop = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = stringResource(R.string.user_profile)
                            )
                        }
                    }
                )

            },
            bottomBar = {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = stringResource(id = screen.titleResId)) } },
                            label = { Text(stringResource(id = screen.titleResId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    BuildMasterTheme {
        DashboardScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DashboardScreenDarkPreview() {
    BuildMasterTheme(darkTheme = true) {
        DashboardScreen()
    }
}