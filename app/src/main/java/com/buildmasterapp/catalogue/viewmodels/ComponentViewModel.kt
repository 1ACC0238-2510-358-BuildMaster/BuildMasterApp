package com.buildmasterapp.catalogue.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.catalogue.data.api.ComponentApi
import com.buildmasterapp.catalogue.domain.model.Category
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.domain.model.Manufacturer
import com.buildmasterapp.catalogue.domain.model.Specifications
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class ComponentViewModel(private val api: ComponentApi) : ViewModel() {
    // Estado para componentes
    private val _components = MutableStateFlow<List<Component>>(emptyList())
    val components: StateFlow<List<Component>> = _components

    // Estado para categorías (dropdown)
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    // Estado para fabricantes (dropdown)
    private val _manufacturers = MutableStateFlow<List<Manufacturer>>(emptyList())
    val manufacturers: StateFlow<List<Manufacturer>> = _manufacturers

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado para mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Carga todos los datos iniciales (componentes, categorías y fabricantes)
    fun loadInitialData(componentId: Long? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Carga en paralelo
                val deferredResponses = awaitAll(
                    async { api.getComponents() },
                    async { api.getCategories() },
                    async { api.getManufacturers() }
                )

                // Procesa respuestas
                val (componentsRes, categoriesRes, manufacturersRes) = deferredResponses

                if (componentsRes.isSuccessful) _components.value = (componentsRes.body() ?: emptyList()) as List<Component>
                if (categoriesRes.isSuccessful) _categories.value = (categoriesRes.body() ?: emptyList()) as List<Category>
                if (manufacturersRes.isSuccessful) _manufacturers.value =
                    (manufacturersRes.body() ?: emptyList()) as List<Manufacturer>

                // Verifica errores individuales
                listOf(componentsRes, categoriesRes, manufacturersRes).forEach { response ->
                    if (!response.isSuccessful) {
                        _errorMessage.value = "Error: ${response.code()} - ${response.errorBody()?.string()}"
                    }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga solo los componentes
    fun loadComponents(
        name: String? = null,
        type: String? = null,
        categoryId: Long? = null,
        manufacturerId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = api.getComponents(
                    name = name,
                    type = type,
                    categoryId = categoryId,
                    manufacturerId = manufacturerId
                )

                if (response.isSuccessful) {
                    _components.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage ?: e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga solo las categorías
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error cargando categorías: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error cargando categorías: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga solo los fabricantes
    fun loadManufacturers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getManufacturers()
                if (response.isSuccessful) {
                    _manufacturers.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error cargando fabricantes: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error cargando fabricantes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
    fun getCategoryById(categoryId: Long): Category? {
        return _categories.value.firstOrNull { it.id == categoryId }
    }

    fun getManufacturerById(manufacturerId: Long): Manufacturer? {
        return _manufacturers.value.firstOrNull { it.id == manufacturerId }
    }

}