package com.buildmasterapp.ui.screens

import androidx.compose.runtime.Composable
import com.buildmasterapp.login.ui.composables.RegisterScreen
import com.buildmasterapp.login.viewmodel.LoginViewModel

@Composable
fun RegistrationScreen(viewModel: LoginViewModel, onBack: () -> Unit) {
    RegisterScreen(viewModel = viewModel, onBack = onBack)
}