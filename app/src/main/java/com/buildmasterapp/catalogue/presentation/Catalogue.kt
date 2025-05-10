package com.buildmasterapp.catalogue.presentation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Catalogue(
    viewModel: ComponentViewModel,
    navController: NavHostController,
    context: android.content.Context = LocalContext.current
) {
    // Corrección 1: Especificar tipos explícitamente para collectAsState
    val components: List<Component> by viewModel.components.collectAsState()
    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val errorMessage: String? by viewModel.errorMessage.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadComponents()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navegar a pantalla de creación */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar componente"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(
                            items = components,
                            key = { component -> component.id ?: 0L }
                        ) { component ->
                            ComponentItem(
                                component = component,
                                onClick = { navController.navigate("details/${component.id}")
                                }
                            )
                        }
                    }
                }
            }
            if (!errorMessage.isNullOrEmpty()) {
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    action = {
                        Button(onClick = { viewModel.loadComponents() }) {
                            Text("Reintentar")
                        }
                    }
                ) {
                    Text(text = errorMessage.orEmpty())
                }
            }
        }
    }
}

@Composable
fun ComponentItem(
    component: Component,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onClick: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded.value = !expanded.value },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5),
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = component.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tipo: ${component.type}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )
            Text(
                text = "Categoría: ${component.category.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )
            Text(
                text = "Fabricante: ${component.manufacturer.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )

            AnimatedVisibility(visible = expanded.value) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Socket: ${component.specifications.socket}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Tipo de Memoria: ${component.specifications.memoryType}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Consumo: ${component.specifications.powerConsumptionWatts}W",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Formato: ${component.specifications.formFactor}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF00B253),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(onClick = onEditClick)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red,
                    modifier = Modifier.clickable(onClick = onDeleteClick)
                )
            }
        }
    }
}
@Composable
fun ComponentDetailsScreen(component: Component) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nombre: ${component.name}", style = MaterialTheme.typography.headlineSmall)
        Text("Tipo: ${component.type}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Socket: ${component.specifications.socket}")
        Text("Tipo de Memoria: ${component.specifications.memoryType}")
        Text("Consumo (W): ${component.specifications.powerConsumptionWatts}")
        Text("Factor de forma: ${component.specifications.formFactor}")

        Spacer(modifier = Modifier.height(8.dp))
        Text("Fabricante: ${component.manufacturer.name}")
        Text("Categoría: ${component.category.name}")
    }
}
