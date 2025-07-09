package com.buildmasterapp.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.buildmasterapp.catalogue.presentation.BuildResultScreen
import com.buildmasterapp.catalogue.presentation.BuildScreen
import com.buildmasterapp.catalogue.presentation.Catalogue
import com.buildmasterapp.catalogue.presentation.SavedBuildsScreen
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModelFactory
import com.buildmasterapp.ui.screens.ChatScreen
import com.buildmasterapp.ui.screens.HomeScreen
import com.buildmasterapp.ui.screens.PricesScreen
import com.buildmasterapp.user.presentation.ProfileScreen
import com.buildmasterapp.user.presentation.ProfileViewModel
import com.buildmasterapp.user.presentation.ProfileUiState
import com.buildmasterapp.user.data.ProfileApi
import com.buildmasterapp.user.data.ProfileRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    // ✅ Aquí se crea una sola vez
    val api = RetrofitClient.api
    val factory = ComponentViewModelFactory(api)
    val componentViewModel: ComponentViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.PCConfig.route) {
            BuildScreen(
                navController = navController,
                viewModel = componentViewModel
            )
        }

        composable("${Screen.Catalogue.route}/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toLongOrNull() ?: 0L

            Catalogue(
                viewModel = componentViewModel,
                navController = navController,
                categoryId = categoryId
            )
        }
        composable("saved_builds") {
            SavedBuildsScreen(
                api = api,
                navController = navController
            )
        }

        composable("build_result/{id}") { backStackEntry ->
            val buildId = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: -1L
            val context = LocalContext.current
            val api = RetrofitClient.api
            BuildResultScreen(buildId = buildId, api = api,
                navController = navController)
        }

        composable(Screen.Chat.route) { ChatScreen() }
        composable(Screen.Prices.route) { PricesScreen() }

        composable(Screen.Profile.route) {
            val client = okhttp3.OkHttpClient.Builder()
                .addInterceptor(com.buildmasterapp.user.data.AuthInterceptor { com.buildmasterapp.user.data.InMemoryTokenHolder.token })
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://buildmaster-api-ddh3asdah2bsggfs.canadacentral-01.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            val api = retrofit.create(ProfileApi::class.java)
            val repository = ProfileRepository(api)
            val viewModel: ProfileViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return ProfileViewModel(repository) as T
                }
            })
            val uiState = viewModel.uiState.collectAsState().value
            LaunchedEffect(Unit) {
                viewModel.loadMe()
            }
            ProfileScreen(
                uiState = uiState,
                onRetry = { viewModel.loadMe() },
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            com.buildmasterapp.ui.composables.SettingsScreen(
                onLogout = {
                    // Aquí puedes limpiar preferencias y navegar a login o auth
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Glossary.route) {
            com.buildmasterapp.ui.screens.GlosaryScreen()
        }

        composable(Screen.Logout.route) {
            LogoutHandler(navController)
        }
    }
}

@Composable
fun GenericScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla: $name")
    }
}

@Composable
fun LogoutHandler(navController: NavHostController) {
    LaunchedEffect(Unit) {
        navController.navigate("auth") {
            popUpTo(0) { inclusive = true }
        }
    }
}