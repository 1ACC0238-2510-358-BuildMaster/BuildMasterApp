package com.buildmasterapp.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.buildmasterapp.catalogue.data.api.RetrofitClient
import com.buildmasterapp.catalogue.presentation.Catalogue
import com.buildmasterapp.catalogue.presentation.ComponentDetailsScreen
import com.buildmasterapp.catalogue.presentation.ComponentFormScreen
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModelFactory
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
            val api = RetrofitClient.instance
            val factory = ComponentViewModelFactory(api)
            val componentViewModel: ComponentViewModel = viewModel(factory = factory)

            Catalogue(componentViewModel, navController, context)
        }

        composable(Screen.ComponentCreate.route) {
            val api = RetrofitClient.instance
            val factory = ComponentViewModelFactory(api)
            val componentViewModel: ComponentViewModel = viewModel(factory = factory)

            ComponentFormScreen(componentViewModel, navController)
        }

        composable(
            Screen.ComponentEdit.route,
            arguments = listOf(navArgument("componentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val componentId = backStackEntry.arguments?.getLong("componentId")
            val api = RetrofitClient.instance
            val factory = ComponentViewModelFactory(api)
            val componentViewModel: ComponentViewModel = viewModel(factory = factory)

            ComponentFormScreen(componentViewModel, navController, componentId)
        }

        composable(
            Screen.ComponentDetails.route,
            arguments = listOf(navArgument("componentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val componentId = backStackEntry.arguments?.getLong("componentId") ?: -1L
            val api = RetrofitClient.instance
            val factory = ComponentViewModelFactory(api)
            val componentViewModel: ComponentViewModel = viewModel(factory = factory)

            val component = componentViewModel.components.collectAsState().value
                .firstOrNull { it.id == componentId }

            if (component != null) {
                ComponentDetailsScreen(component = component)
            } else {
                Text("Componente no encontrado")
            }
        }
        composable(Screen.Chat.route) {
            ChatScreen() }


        composable(Screen.Prices.route) {
            PricesScreen()
        }
        composable("catalogue_home") {
            Catalogue(viewModel = viewModel(), navController = navController)
        }

        composable("create") {
            ComponentFormScreen(viewModel = viewModel(), navController = navController)
        }

        composable("edit/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            ComponentFormScreen(viewModel = viewModel(), navController = navController, componentId = id)
        }

        composable("details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            val api = RetrofitClient.instance
            val factory = ComponentViewModelFactory(api)
            val componentViewModel: ComponentViewModel = viewModel(factory = factory)

            val components = componentViewModel.components.collectAsState().value
            val component = components.firstOrNull { it.id == id }

            if (component != null) {
                ComponentDetailsScreen(component = component)
            } else {
                Text("Componente no encontrado")
            }
        }

        // Rutas para el Navigation Drawer (pueden ser las mismas u otras)
        composable(Screen.Profile.route) { GenericScreen(name = stringResource(id = Screen.Profile.titleResId)) }
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