package com.buildmasterapp.catalogue.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadCategories()

        val sharedPrefs = context.getSharedPreferences(
            "my_prefs",
            android.content.Context.MODE_PRIVATE
        )
        val lastBuildId = sharedPrefs.getLong("latest_build_id", -1L)
        if (lastBuildId != -1L) {
            println("ℹ️ Última Build ID guardada: $lastBuildId")
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Configura tu Build") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF495D92),
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFF5F5F5),
                tonalElevation = 8.dp,
                modifier = Modifier.height(80.dp) // Altura fija para los botones
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End, // Alineación a la derecha
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón "Guardar Build"
                    FilledTonalButton(
                        onClick = {
                            if (selectedComponents.size < 8) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Selecciona al menos 8 componentes antes de guardar."
                                    )
                                }
                            } else {
                                viewModel.saveBuild(context)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Build guardado correctamente."
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar Build")
                    }

                    // Botón "Ver Builds"
                    FilledTonalButton(
                        onClick = { navController.navigate("saved_builds") },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF495D92),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver Builds")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección superior "Resetear"
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
                        Icon(Icons.Default.Refresh, contentDescription = "Resetear")
                        Spacer(Modifier.width(4.dp))
                        Text("Resetear")
                    }
                }
            }

            // Grid de categorías
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),  // ✅ Asegura que el grid ocupe todo el espacio disponible
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        onClick = { navController.navigate("catalogue/${category.id}") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedComponents.containsKey(category.id))
                                Color(0xFF4CAF50) else Color.White
                        )
                    ) {
                        Text(
                            text = category.name,
                            modifier = Modifier.fillMaxSize().wrapContentSize(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}