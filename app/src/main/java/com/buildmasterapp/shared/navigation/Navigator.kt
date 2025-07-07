package com.buildmasterapp.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buildmasterapp.catalogue.presentation.Catalogue
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel

@Composable
fun Navigator(
    catalogueViewModel: ComponentViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "Catalogue"
    ) {
        composable("Catalogue") {
            Catalogue(
                viewModel = catalogueViewModel,
                navController = navController,
                context = context
            )
        }
    }
}