package com.buildmasterapp.shared.navigation

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buildmasterapp.catalogue.presentation.Catalogue
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.buildmasterapp.catalogue.presentation.ComponentDetailsScreen
import com.buildmasterapp.catalogue.presentation.ComponentFormScreen


@Composable
fun Navigator(viewModel: ComponentViewModel, context: Context) {
    val navController = rememberNavController()
    val componentsState = viewModel.components.collectAsState()

    NavHost(navController = navController, startDestination = "Catalogue") {
        composable("Catalogue") {
            Catalogue(viewModel, navController, context)
        }
        composable("create") {
            ComponentFormScreen(viewModel, navController)
        }
        composable(
            route = "edit/{componentId}",
            arguments = listOf(navArgument("componentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val componentId = backStackEntry.arguments?.getLong("componentId")
            ComponentFormScreen(viewModel, navController, componentId)
        }
        composable(
            route = "details/{componentId}",
            arguments = listOf(navArgument("componentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val componentId = backStackEntry.arguments?.getLong("componentId") ?: -1L
            val component = componentsState.value.firstOrNull { component -> component.id == componentId }

            if (component != null) {
                ComponentDetailsScreen(component = component)
            } else {
                Text("Componente no encontrado")
            }
        }
    }
}