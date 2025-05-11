package com.buildmasterapp.catalogue.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildmasterapp.catalogue.domain.model.Category
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.domain.model.Manufacturer
import com.buildmasterapp.catalogue.domain.model.Specifications
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel

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
    val expanded = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        viewModel.loadComponents()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Agregar componente")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(items = components) { component ->
                        ComponentItem(
                            component = component,
                            onEditClick = { navController.navigate("edit/${component.id}") },
                            onDeleteClick = {
                                viewModel.deleteComponent(component.id ?: -1L) {}
                            },
                            onClick = { navController.navigate("details/${component.id}") }
                        )
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
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentFormScreen(
    viewModel: ComponentViewModel,
    navController: NavHostController,
    componentId: Long? = null
) {
    // Estados del ViewModel
    val components by viewModel.components.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val manufacturers by viewModel.manufacturers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Estados del formulario
    val (name, setName) = remember { mutableStateOf("") }
    val (type, setType) = remember { mutableStateOf("") }
    val (price, setPrice) = remember { mutableStateOf("") }
    val (socket, setSocket) = remember { mutableStateOf("") }
    val (memoryType, setMemoryType) = remember { mutableStateOf("") }
    val (powerConsumption, setPowerConsumption) = remember { mutableStateOf("") }
    val (formFactor, setFormFactor) = remember { mutableStateOf("") }

    // Estados para los dropdowns
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedManufacturer by remember { mutableStateOf<Manufacturer?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var manufacturerExpanded by remember { mutableStateOf(false) }

    // Cargar datos iniciales
    LaunchedEffect(componentId) {
        viewModel.loadInitialData(componentId)

        // Precargar datos si estamos editando
        componentId?.let { id ->
            components.firstOrNull { it.id == id }?.let { component ->
                setName(component.name)
                setType(component.type)
                setPrice(component.price.toString())
                setSocket(component.specifications.socket)
                setMemoryType(component.specifications.memoryType)
                setPowerConsumption(component.specifications.powerConsumptionWatts.toString())
                setFormFactor(component.specifications.formFactor)
                selectedCategory = component.category
                selectedManufacturer = component.manufacturer
            }
        }
    }

    // Validación del formulario
    val isFormValid = name.isNotBlank() &&
            type.isNotBlank() &&
            price.isNotBlank() &&
            selectedCategory != null &&
            selectedManufacturer != null &&
            socket.isNotBlank() &&
            formFactor.isNotBlank()

    // Manejo de errores
    if (!errorMessage.isNullOrEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.clearErrorMessage() },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearErrorMessage() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (componentId != null) "Editar Componente" else "Nuevo Componente") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            // Verifica que las entidades seleccionadas existan
                            val categoryToSend = selectedCategory?.let { viewModel.getCategoryById(it.id) }
                            val manufacturerToSend = selectedManufacturer?.let { viewModel.getManufacturerById(it.id) }

                            when {
                                categoryToSend == null -> {
                                    viewModel.setErrorMessage("Seleccione una categoría válida")
                                    return@Button
                                }

                                manufacturerToSend == null -> {
                                    viewModel.setErrorMessage("Seleccione un fabricante válido")
                                    return@Button
                                }

                                else -> {
                                    val newComponent = Component(
                                        id = componentId,
                                        name = name,
                                        type = type,
                                        price = price.toDoubleOrNull() ?: 0.0,
                                        category = selectedCategory ?: Category(0L, ""),
                                        manufacturer = selectedManufacturer ?: Manufacturer(0L, ""),
                                        specifications = Specifications(
                                            socket = socket,
                                            memoryType = memoryType,
                                            powerConsumptionWatts = powerConsumption.toIntOrNull()
                                                ?: 0,
                                            formFactor = formFactor
                                        )
                                    )

                                    if (componentId != null) {
                                        viewModel.updateComponent(newComponent) {
                                            navController.popBackStack()
                                        }
                                    } else {
                                        viewModel.createComponent(newComponent) {
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading && isFormValid,
                        modifier = Modifier.height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(if (componentId != null) "Guardar Cambios" else "Crear Componente")
                        }
                        println("Enviando categoría con ID: ${selectedCategory?.id}")
                        println("Enviando fabricante con ID: ${selectedManufacturer?.id}")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección Información Básica
            Text(
                text = "Información Básica",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = setName,
                label = { Text("Nombre del Componente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = type,
                onValueChange = setType,
                label = { Text("Tipo de Componente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = price,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) setPrice(it) },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                prefix = { Text("$") }
            )

            // Selector de Categoría
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 8.dp),
                    readOnly = true,
                    value = selectedCategory?.name ?: "Seleccione una categoría",
                    onValueChange = {},
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Selector de Fabricante
            ExposedDropdownMenuBox(
                expanded = manufacturerExpanded,
                onExpandedChange = { manufacturerExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 16.dp),
                    readOnly = true,
                    value = selectedManufacturer?.name ?: "Seleccione un fabricante",
                    onValueChange = {},
                    label = { Text("Fabricante") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = manufacturerExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = manufacturerExpanded,
                    onDismissRequest = { manufacturerExpanded = false }
                ) {
                    manufacturers.forEach { manufacturer ->
                        DropdownMenuItem(
                            text = { Text(manufacturer.name) },
                            onClick = {
                                selectedManufacturer = manufacturer
                                manufacturerExpanded = false
                            }
                        )
                    }
                }
            }

            // Sección Especificaciones
            Text(
                text = "Especificaciones Técnicas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = socket,
                onValueChange = setSocket,
                label = { Text("Socket/Tipo de Conector") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = memoryType,
                onValueChange = setMemoryType,
                label = { Text("Tipo de Memoria Compatible") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = powerConsumption,
                onValueChange = { if (it.matches(Regex("^\\d*$"))) setPowerConsumption(it) },
                label = { Text("Consumo Energético (W)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                suffix = { Text("W") }
            )

            OutlinedTextField(
                value = formFactor,
                onValueChange = setFormFactor,
                label = { Text("Factor de Forma") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}