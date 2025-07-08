package com.buildmasterapp.catalogue.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buildmasterapp.catalogue.data.api.ComponentApi
import com.buildmasterapp.catalogue.domain.model.BuildResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildResultScreen(
    buildId: Long,
    api: ComponentApi,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var result by remember { mutableStateOf<BuildResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(buildId) {
        isLoading = true
        scope.launch {
            try {
                val response = api.getBuildResult(buildId)
                if (response.isSuccessful) {
                    result = response.body()
                } else {
                    errorMessage = "No se pudo cargar los resultados (Error ${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Build") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF495D92),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando resultados...")
                    }
                }

                errorMessage != null -> {
                    ErrorMessage(message = errorMessage!!)
                }

                result != null -> {
                    BuildResultDetails(result = result!!)
                }

                else -> {
                    ErrorMessage(message = "No se encontraron resultados")
                }
            }
        }
    }
}

@Composable
fun BuildResultDetails(result: BuildResult) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen de la Build",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(label = "ID de Build:", value = result.buildId.toString())
                    InfoRow(label = "Rendimiento:", value = result.estimatedPerformance)
                    InfoRow(label = "Consumo energético:", value = "${result.powerConsumptionWatts} W")
                    InfoRow(label = "Precio estimado:", value = "$${result.estimatedPrice}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Observaciones:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.observations,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        item {
            FilledTonalButton(
                onClick = { /* Acción compartir */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Compartir esta Build")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(150.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}