package com.buildmasterapp.catalogue.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.catalogue.data.api.ComponentApi
import com.buildmasterapp.catalogue.domain.model.Component
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComponentViewModel(private val api: ComponentApi) : ViewModel() {
    private val _components = MutableStateFlow<List<Component>>(emptyList())
    val components: StateFlow<List<Component>> = _components

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadComponents() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = api.getAllComponents()
                if (response.isSuccessful) {
                    _components.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createComponent(component: Component, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.createComponent(component)
                if (response.isSuccessful) {
                    loadComponents() // Recargar lista
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al crear: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}