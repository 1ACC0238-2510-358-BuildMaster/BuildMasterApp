package com.buildmasterapp.provider.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.provider.data.datasource.SimulatedPriceDataSource
import com.buildmasterapp.provider.data.model.ComponentPriceInfo
import com.buildmasterapp.provider.data.repository.PriceRepository


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PriceScreenUiState(
    val components: List<ComponentPriceInfo> = ComponentPriceInfo.getInitialComponents(),
    val isLoadingOverall: Boolean = false,
    val error: String? = null
)

class PriceViewModel(

    private val repository: PriceRepository = PriceRepository(SimulatedPriceDataSource())
) : ViewModel() {

    private val _uiState = MutableStateFlow(PriceScreenUiState())
    val uiState: StateFlow<PriceScreenUiState> = _uiState.asStateFlow()

    init {
        loadAllPrices()
    }

    fun loadAllPrices() {
        if (_uiState.value.isLoadingOverall) return
        _uiState.update { it.copy(isLoadingOverall = true, error = null) }

        viewModelScope.launch {
            try {
                val initialComponents = ComponentPriceInfo.getInitialComponents()
                val updatedComponents = initialComponents.map { component ->

                    _uiState.update { currentState ->
                        currentState.copy(components = currentState.components.map { c ->
                            if (c.id == component.id) c.copy(isLoading = true) else c
                        })
                    }
                    val processedInfo = repository.getProcessedComponentPriceInfo(component)

                    _uiState.update { currentState ->
                        currentState.copy(components = currentState.components.map { c ->
                            if (c.id == processedInfo.id) processedInfo.copy(isLoading = false) else c
                        })
                    }
                    processedInfo
                }
                _uiState.update { it.copy(components = updatedComponents, isLoadingOverall = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar precios: ${e.message}", isLoadingOverall = false) }
            }
        }
    }

    fun refreshComponentPrice(componentId: String) {
        val componentToRefresh = _uiState.value.components.find { it.id == componentId } ?: return
        if (componentToRefresh.isLoading) return

        _uiState.update { currentState ->
            currentState.copy(
                components = currentState.components.map {
                    if (it.id == componentId) it.copy(isLoading = true) else it
                },
                error = null
            )
        }

        viewModelScope.launch {
            try {
                val processedInfo = repository.getProcessedComponentPriceInfo(componentToRefresh)
                _uiState.update { currentState ->
                    currentState.copy(
                        components = currentState.components.map {
                            if (it.id == processedInfo.id) processedInfo.copy(isLoading = false) else it
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        components = currentState.components.map {
                            if (it.id == componentId) it.copy(isLoading = false, priceRange = "Error") else it
                        },
                        error = "Error actualizando ${componentToRefresh.name}"
                    )
                }
            }
        }
    }
}