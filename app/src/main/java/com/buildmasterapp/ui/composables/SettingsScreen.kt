package com.buildmasterapp.ui.composables

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var notificationsEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("notifications", true)) }
    val themeOptions = mapOf("system" to "Predeterminado del sistema", "light" to "Claro", "dark" to "Oscuro")
    var selectedTheme by remember { mutableStateOf(sharedPreferences.getString("theme", "system") ?: "system") }
    var themeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Notificaciones",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    sharedPreferences.edit().putBoolean("notifications", it).apply()
                }
            )
        }

        Text(
            text = "Tema",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = themeDropdownExpanded,
            onExpandedChange = { themeDropdownExpanded = !themeDropdownExpanded }
        ) {
            OutlinedTextField(
                value = themeOptions[selectedTheme] ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = themeDropdownExpanded,
                onDismissRequest = { themeDropdownExpanded = false }
            ) {
                themeOptions.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            selectedTheme = key
                            sharedPreferences.edit().putString("theme", key).apply()
                            themeDropdownExpanded = false
                            // TODO: Aplicar el cambio de tema en toda la app
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cerrar sesión")
        }
    }
}
