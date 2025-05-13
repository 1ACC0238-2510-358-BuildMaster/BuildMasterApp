package com.buildmasterapp.shared.navigation

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.buildmasterapp.catalogue.presentation.Catalogue
import com.buildmasterapp.catalogue.presentation.ComponentDetailsScreen
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel

@Composable
fun Navigator(

    catalogueViewModel: ComponentViewModel = viewModel(),

) {
    val navController = rememberNavController()

    val componentsState = catalogueViewModel.components.collectAsState()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "Catalogue") {
        composable("Catalogue") {

            Catalogue(catalogueViewModel, navController, context)
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