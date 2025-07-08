package com.buildmasterapp.catalogue.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen(
    navController: NavController,
    viewModel: ComponentViewModel
) {
    val categories by viewModel.categories.collectAsState()
    val selectedComponents by viewModel.selectedComponents.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configura tu Build") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (selectedComponents.size < 8) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Selecciona al menos 8 componentes antes de guardar."
                                )
                            }
                        } else {
                            viewModel.saveBuild()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Build guardado correctamente."
                                )
                            }
                        }
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Text("Guardar Build", color = Color.White)
                }

                FloatingActionButton(
                    onClick = {
                        navController.navigate("saved_builds")
                    },
                    containerColor = Color(0xFF2196F3)
                ) {
                    Text("Ver Builds", color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🟢 Sección superior: Resetear selección
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEEEEEE))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Categorías de componentes", style = MaterialTheme.typography.titleMedium)
                    TextButton(
                        onClick = { viewModel.resetBuild() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Resetear")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🟢 Grid de categorías
            if (categories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedComponents.containsKey(category.id)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF4CAF50) else Color.White,
                                contentColor = if (isSelected) Color.White else Color.Black
                            ),
                            onClick = {
                                navController.navigate("catalogue/${category.id}")
                            }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}