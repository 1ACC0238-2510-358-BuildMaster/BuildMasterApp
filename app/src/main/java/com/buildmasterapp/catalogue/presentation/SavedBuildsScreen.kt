package com.buildmasterapp.catalogue.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buildmasterapp.catalogue.data.api.ComponentApi
import com.buildmasterapp.catalogue.domain.model.Build
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedBuildsScreen(
    api: ComponentApi,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var builds by remember { mutableStateOf<List<Build>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("my_prefs", android.content.Context.MODE_PRIVATE)
        val savedIds = sharedPrefs.getStringSet("saved_build_ids", emptySet())?.mapNotNull { it.toLongOrNull() } ?: emptyList()

        if (savedIds.isEmpty()) {
            errorMessage = "No hay Builds guardadas aún."
            return@LaunchedEffect
        }

        isLoading = true
        val tempList = mutableListOf<Build>()
        scope.launch {
            try {
                for (id in savedIds) {
                    val response = api.getBuildById(id)
                    if (response.isSuccessful) {
                        response.body()?.let { tempList.add(it) }
                    }
                }
                builds = tempList
            } catch (e: Exception) {
                errorMessage = "Excepción: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = { Text("Tus Builds Guardadas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando Builds...")
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                builds.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay Builds disponibles.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(builds) { build ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("build_result/${build.id}")
                                    },
                                elevation = CardDefaults.cardElevation(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Build ID: ${build.id}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Componentes: ${build.componentIds.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Creado en: ${formatDate(build.createdAt)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val date = parser.parse(dateString)
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatter.format(date!!)
    } catch (e: Exception) {
        try {
            // Si falla, intenta con milisegundos normales
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            formatter.format(date!!)
        } catch (_: Exception) {
            dateString // Si todo falla, devuélvelo crudo
        }
    }
}