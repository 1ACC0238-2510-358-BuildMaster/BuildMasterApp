package com.buildmasterapp.catalogue.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    errorMessage = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Excepción: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado de la Build") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Text(errorMessage!!)
                }
                result != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Build ID: ${result!!.buildId}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Rendimiento estimado: ${result!!.estimatedPerformance}")
                            Text("Consumo energético: ${result!!.powerConsumptionWatts} W")
                            Text("Precio estimado: $${result!!.estimatedPrice}")
                            Text("Observaciones: ${result!!.observations}")
                        }
                    }
                }
                else -> {
                    Text("No se encontró resultado para esta Build.")
                }
            }
        }
    }
}