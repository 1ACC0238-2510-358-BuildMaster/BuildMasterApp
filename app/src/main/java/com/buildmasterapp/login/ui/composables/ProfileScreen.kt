package com.buildmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildmasterapp.login.viewmodel.LoginViewModel

@Composable
fun ProfileScreen(
    viewModel: LoginViewModel,
    navController: NavHostController
) {
    val user = viewModel.user // Assume `user` contains profile data like name and email

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome, ${user?.name ?: "User"}!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Email: ${user?.email ?: "Not available"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    viewModel.signOff() // Clear user session or token
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true } // Clear back stack
                    }
                }
            ) {
                Text("Sign Off")
            }
        }
    }
}