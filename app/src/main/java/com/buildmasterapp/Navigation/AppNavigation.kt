package com.buildmasterapp.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModelFactory
import com.buildmasterapp.catalogue.presentation.Catalogue
import com.buildmasterapp.shared.navigation.Navigator
import com.buildmasterapp.ui.screens.ChatScreen
import com.buildmasterapp.ui.screens.HomeScreen
import com.buildmasterapp.ui.screens.PricesScreen


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {

        composable(Screen.Home.route) { HomeScreen() }


        composable(Screen.PCConfig.route) {
            val context = LocalContext.current
            val api = RetrofitClient.api
            val factory = ComponentViewModelFactory(api)
            val componentViewModel: ComponentViewModel = viewModel(factory = factory)
            Catalogue(
                viewModel = componentViewModel,
                navController = navController,
                context = context
            )
        }
        composable(Screen.Chat.route) { ChatScreen() }


        composable(Screen.Prices.route) {
            PricesScreen()
        }

        // Rutas para el Navigation Drawer (pueden ser las mismas u otras)
        composable(Screen.Profile.route) { GenericScreen(name = stringResource(id = Screen.Profile.titleResId)) }
        composable(Screen.Settings.route) { GenericScreen(name = stringResource(id = Screen.Settings.titleResId)) }
        composable(Screen.Language.route) { GenericScreen(name = stringResource(id = Screen.Language.titleResId)) }
        composable(Screen.Glossary.route) { com.buildmasterapp.ui.screens.GlosaryScreen() }
        composable(Screen.Logout.route) {
            // Aquí puedes limpiar el estado de autenticación y navegar a login
            LogoutHandler(navController)
        }
    }
}

@Composable
fun GenericScreen(name: String) { // Esta función auxiliar se mantiene igual
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla: $name")
    }
}

@Composable
fun LogoutHandler(navController: NavHostController) {
    // Aquí puedes limpiar cualquier estado de autenticación, tokens, etc.
    // Luego navega a la pantalla de autenticación principal
    LaunchedEffect(Unit) {
        navController.navigate("auth") {
            popUpTo(0) { inclusive = true }
        }
    }
}
