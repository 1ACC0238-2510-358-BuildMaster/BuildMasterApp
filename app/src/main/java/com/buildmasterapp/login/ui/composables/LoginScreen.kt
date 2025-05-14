package com.buildmasterapp.login.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.buildmasterapp.login.viewmodel.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )
        Button(onClick = { viewModel.login(email, password) }) {
            Text("Iniciar Sesión")
        }
        Button(onClick = { viewModel.register(email, password, "Usuario") }) {
            Text("Registrarse")
        }
        viewModel.token?.let { token: String ->
            Text("Token: $token")
        }
        viewModel.errorMessage?.let { errorMessage: String ->
            Text("Error: $errorMessage", color = Color.Red)
        }
    }
}