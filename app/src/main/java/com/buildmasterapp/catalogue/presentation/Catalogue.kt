package com.buildmasterapp.catalogue.presentation

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Catalogue(
    viewModel: ComponentViewModel,
    navController: NavHostController,
    context: android.content.Context = LocalContext.current
) {
    val components: List<Component> by viewModel.components.collectAsState()
    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val errorMessage: String? by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        val fakeCategoryId = 1L
        viewModel.loadComponents(categoryId = fakeCategoryId)
    }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedManufacturer by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredComponents = components.filter {
        it.name.contains(searchQuery, ignoreCase = true) &&
                (selectedType == null || it.type == selectedType) &&
                (selectedManufacturer == null || it.manufacturer.name == selectedManufacturer)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Componentes") }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), // Altura basada en el contenido
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filtro de Tipo (más compacto)
                Box(
                    modifier = Modifier
                        .weight(0.8f) // Peso reducido para Tipo
                        .padding(end = 4.dp)
                ) {
                    FilterDropdown(
                        value = selectedType,
                        hint = "Tipo",
                        items = components.map { it.type }.distinct(),
                        labelBuilder = { it },
                        onChanged = { selectedType = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Filtro de Fabricante (más ancho)
                Box(
                    modifier = Modifier
                        .weight(1.2f) // Peso aumentado para Fabricante
                        .padding(horizontal = 4.dp)
                ) {
                    FilterDropdown(
                        value = selectedManufacturer,
                        hint = "Fabricante",
                        items = components.map { it.manufacturer.name }.distinct(),
                        labelBuilder = { it },
                        onChanged = { selectedManufacturer = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Botón Quitar filtros (compacto)
                Button(
                    onClick = {
                        selectedType = null
                        selectedManufacturer = null
                        searchQuery = ""
                    },
                    modifier = Modifier
                        .width(100.dp) // Ancho reducido
                        .height(40.dp), // Altura compacta
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp) // Padding interno reducido
                ) {
                    Text(
                        "Quitar filtros",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            SearchField(
                initialValue = "",
                onChanged = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    !errorMessage.isNullOrEmpty() -> {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    filteredComponents.isEmpty() -> {
                        Text(
                            text = "No hay componentes para mostrar",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyColumn(contentPadding = PaddingValues(8.dp)) {
                            items(filteredComponents) { component ->
                                ComponentItem(
                                    component = component,
                                    onClick = {
                                        // Aquí puedes abrir detalles o acción
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ComponentItem(
    component: Component,
    onClick: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {}, // Deshabilitado
                onLongClick = { expanded.value = !expanded.value }
            ),
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
        }
    }
}
@Composable
fun SearchField(
    initialValue: String = "",
    onChanged: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onChanged(it.lowercase())
        },
        label = { Text("Buscar por nombre") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterDropdown(
    value: T?,
    hint: String,
    items: List<T>,
    labelBuilder: (T) -> String,
    onChanged: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            value = value?.let { labelBuilder(it) } ?: "",
            onValueChange = {},
            label = { Text(hint, fontSize = 12.sp) }, // Tamaño de texto reducido
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp), // Texto más pequeño
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            labelBuilder(item),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onChanged(item)
                        expanded = false
                    }
                )
            }
        }
    }
}