package com.buildmasterapp.user.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buildmasterapp.user.data.AuthApiService
import com.buildmasterapp.user.data.DataStoreManager
import com.buildmasterapp.user.domain.AuthRepository
import com.buildmasterapp.user.domain.LoginUseCase
import com.buildmasterapp.user.domain.RegisterUseCase
import com.buildmasterapp.ui.composables.DashboardScreen
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
}

@Composable
fun AuthNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Retrofit y dependencias
    val retrofit = Retrofit.Builder()
        .baseUrl("https://buildmaster-api-ddh3asdah2bsggfs.canadacentral-01.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api = retrofit.create(AuthApiService::class.java)
    val repository = AuthRepository(api)
    val loginUseCase = LoginUseCase(repository)
    val registerUseCase = RegisterUseCase(repository)
    val dataStoreManager = DataStoreManager(context)

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(loginUseCase, registerUseCase, dataStoreManager)
    )

    val uiState = viewModel.uiState.collectAsState()

    // Navegación que comienza en Login
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onLogin = { email, pass -> viewModel.login(email, pass) }, // Cambiado a email
                isLoading = uiState.value is AuthUiState.Loading,
                errorMessage = (uiState.value as? AuthUiState.Error)?.message,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )

            // Navegar al dashboard cuando el login sea exitoso
            if (uiState.value is AuthUiState.Success) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                onRegister = { user, email, pass -> viewModel.register(user, email, pass) },
                isLoading = uiState.value is AuthUiState.Loading,
                errorMessage = (uiState.value as? AuthUiState.Error)?.message,
                onNavigateToLogin = {
                    viewModel.resetState()
                    navController.popBackStack()
                }
            )

            // Navegar al dashboard cuando el registro sea exitoso
            if (uiState.value is AuthUiState.Success) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}
