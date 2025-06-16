package com.buildmasterapp.ui.screens

import androidx.compose.runtime.*
import com.buildmasterapp.login.ui.composables.LoginScreen
import com.buildmasterapp.login.viewmodel.LoginViewModel

@Composable
fun ProfileScreen(viewModel: LoginViewModel, onNavigateToRegister: () -> Unit) {
    LoginScreen(viewModel = viewModel, onNavigateToRegister = onNavigateToRegister)
}