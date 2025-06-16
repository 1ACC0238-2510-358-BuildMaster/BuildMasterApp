package com.buildmasterapp.login.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.buildmasterapp.login.viewmodel.LoginViewModel
import java.util.UUID

@Composable
fun RegisterScreen(viewModel: LoginViewModel, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var biografy by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }

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
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = biografy,
            onValueChange = { biografy = it },
            label = { Text("Biography (Optional)") }
        )
        TextField(
            value = fotoUrl,
            onValueChange = { fotoUrl = it },
            label = { Text("Photo URL (Optional)") }
        )
        Button(onClick = {
            val id = UUID.randomUUID()
            viewModel.register(
                email = email,
                password = password,
                nombre = name,
                biografy = biografy.takeIf { it.isNotBlank() },
                fotoUrl = fotoUrl.takeIf { it.isNotBlank() }
            )
        }) {
            Text("Register")
        }
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}