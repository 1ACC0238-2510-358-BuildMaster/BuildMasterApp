package com.buildmasterapp.catalogue.viewmodels

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
                    async { api.getAllComponents() },
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
    fun loadComponents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getAllComponents()
                if (response.isSuccessful) {
                    _components.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error cargando componentes: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error cargando componentes: ${e.message}"
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
    @Serializable
    data class ComponentCreateRequest(
        val name: String,
        val type: String,
        val price: Double,
        val categoryId: Long,
        val manufacturerId: Long,
        val specifications: Specifications
    )

    fun createComponent(component: Component, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (component.category.id == null || _categories.value.none { it.id == component.category.id }) {
                    _errorMessage.value = "Seleccione una categoría válida"
                    return@launch
                }

                if (component.manufacturer.id == null || _manufacturers.value.none { it.id == component.manufacturer.id }) {
                    _errorMessage.value = "Seleccione un fabricante válido"
                    return@launch
                }

                val createRequest = ComponentCreateRequest(
                    name = component.name,
                    type = component.type,
                    price = component.price,
                    categoryId = component.category.id,
                    manufacturerId = component.manufacturer.id,
                    specifications = component.specifications
                )

                val response = api.createComponent(createRequest)

                if (response.isSuccessful) {
                    loadComponents()
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al crear: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateComponent(component: Component, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Crea un DTO simple para la actualización
                val updateRequest = ComponentUpdateRequest(
                    id = component.id,
                    name = component.name,
                    type = component.type,
                    price = component.price,
                    categoryId = component.category.id, // Solo envía el ID
                    manufacturerId = component.manufacturer.id, // Solo envía el ID
                    specifications = component.specifications
                )

                val response = api.updateComponent(component.id ?: -1L, updateRequest)
                if (response.isSuccessful) {
                    loadComponents()
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al actualizar: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Añade esta data class
    @Serializable
    data class ComponentUpdateRequest(
        val id: Long?,
        val name: String,
        val type: String,
        val price: Double,
        val categoryId: Long,  // Solo el ID
        val manufacturerId: Long,  // Solo el ID
        val specifications: Specifications
    )

    // Elimina un componente
    fun deleteComponent(componentId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.deleteComponent(componentId)
                /*if (response.isSuccessful) {
                    _errorMessage.value = "Componente eliminado exitosamente"
                    loadComponents() // Recarga la lista actualizada
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al eliminar: ${response.code()}"
                }*/
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
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