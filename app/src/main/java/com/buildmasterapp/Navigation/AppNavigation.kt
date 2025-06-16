package com.buildmasterapp.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.buildmasterapp.catalogue.data.api.RetrofitClient
import com.buildmasterapp.login.data.api.RetrofitClientLogin
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModelFactory
import com.buildmasterapp.login.viewmodel.LoginViewModel
import com.buildmasterapp.login.viewmodel.LoginViewModelFactory
import com.buildmasterapp.shared.navigation.Navigator
import com.buildmasterapp.ui.screens.ChatScreen
import com.buildmasterapp.ui.screens.HomeScreen
import com.buildmasterapp.ui.screens.PricesScreen
import com.buildmasterapp.ui.screens.ProfileScreen
import com.buildmasterapp.ui.screens.RegistrationScreen

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
            val api = RetrofitClient.instance // o la instancia real según cómo lo implementaste
            val factory = ComponentViewModelFactory(api)

            val componentViewModel: ComponentViewModel = viewModel(factory = factory)

            Navigator(catalogueViewModel = componentViewModel)
        }
        composable(Screen.Chat.route) { ChatScreen() }


        composable(Screen.Prices.route) {
            PricesScreen()
        }

        // Rutas para el Navigation Drawer (pueden ser las mismas u otras)
        composable(Screen.Profile.route) {
            val context = LocalContext.current
            val api = RetrofitClientLogin.instance
            val factory = LoginViewModelFactory(api)
            val loginViewModel: LoginViewModel = viewModel(factory = factory)

            ProfileScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Registration.route) }
            )
        }

        composable(Screen.Registration.route) {
            val context = LocalContext.current
            val api = RetrofitClientLogin.instance
            val factory = LoginViewModelFactory(api)
            val loginViewModel: LoginViewModel = viewModel(factory = factory)

            RegistrationScreen(
                viewModel = loginViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) { GenericScreen(name = stringResource(id = Screen.Settings.titleResId)) }
        composable(Screen.Language.route) { GenericScreen(name = stringResource(id = Screen.Language.titleResId)) }
        composable(Screen.Support.route) { GenericScreen(name = stringResource(id = Screen.Support.titleResId)) }
        composable(Screen.Logout.route) {
            GenericScreen(name = stringResource(id = Screen.Logout.titleResId))
        }
    }
}

@Composable
fun GenericScreen(name: String) { // Esta función auxiliar se mantiene igual
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla: $name")
    }
}