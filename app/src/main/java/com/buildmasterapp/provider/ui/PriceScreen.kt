package com.buildmasterapp.provider.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buildmasterapp.provider.ui.composables.ComponentPriceCard
import com.buildmasterapp.provider.viewmodel.PriceScreenUiState
import com.buildmasterapp.provider.viewmodel.PriceViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceScreen(
    priceViewModel: PriceViewModel = viewModel()
) {
    val uiState by priceViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparador de Precios") },
                actions = {
                    if (uiState.isLoadingOverall) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(end = 16.dp))
                    } else {
                        IconButton(onClick = { priceViewModel.loadAllPrices() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refrescar Todo")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        PriceScreenContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onRefreshComponent = { componentId ->
                priceViewModel.refreshComponentPrice(componentId)
            },
            onRefreshAll = { priceViewModel.loadAllPrices() }
        )
    }
}

@Composable
fun PriceScreenContent(
    modifier: Modifier = Modifier,
    uiState: PriceScreenUiState,
    onRefreshComponent: (String) -> Unit,
    onRefreshAll: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.error != null) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.components, key = { it.id }) { componentInfo ->
                ComponentPriceCard(
                    componentInfo = componentInfo,
                    onRefreshClicked = onRefreshComponent
                )
            }

            if (uiState.components.isEmpty() && !uiState.isLoadingOverall) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay componentes para mostrar.")
                    }
                }
            }
        }
    }
}