package com.buildmasterapp.catalogue.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.catalogue.data.api.ComponentApi
import com.buildmasterapp.catalogue.domain.model.BuildCreateRequest
import com.buildmasterapp.catalogue.domain.model.Category
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.domain.model.Manufacturer
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class ComponentViewModel(private val api: ComponentApi) : ViewModel() {
    // ✅ Estado para componentes
    private val _components = MutableStateFlow<List<Component>>(emptyList())
    val components: StateFlow<List<Component>> = _components

    // ✅ Estado para categorías
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    // ✅ Estado para fabricantes
    private val _manufacturers = MutableStateFlow<List<Manufacturer>>(emptyList())
    val manufacturers: StateFlow<List<Manufacturer>> = _manufacturers

    // ✅ Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ✅ Estado para error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // ✅ Estado para componentes seleccionados (Build)
    private val _selectedComponents = MutableStateFlow<Map<Long, Component>>(emptyMap())
    val selectedComponents: StateFlow<Map<Long, Component>> = _selectedComponents
    // ----------------------------------------------------
    // ✅ Métodos de carga
    // ----------------------------------------------------

    fun loadInitialData(componentId: Long? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val deferredResponses = awaitAll(
                    async { api.getComponents() },
                    async { api.getCategories() },
                    async { api.getManufacturers() }
                )

                val (componentsRes, categoriesRes, manufacturersRes) = deferredResponses

                if (componentsRes.isSuccessful) _components.value = ((componentsRes.body() ?: emptyList()) as List<Component>)
                if (categoriesRes.isSuccessful) _categories.value = ((categoriesRes.body() ?: emptyList()) as List<Category>)
                if (manufacturersRes.isSuccessful) _manufacturers.value = ((manufacturersRes.body() ?: emptyList()) as List<Manufacturer>)

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

    // ----------------------------------------------------
    // ✅ Métodos para Build
    // ----------------------------------------------------

    fun selectComponent(categoryId: Long, component: Component) {
        _selectedComponents.value = _selectedComponents.value.toMutableMap().apply {
            put(categoryId, component)
        }
    }
    fun removeComponent(categoryId: Long) {
        _selectedComponents.value = _selectedComponents.value.toMutableMap().apply {
            remove(categoryId)
        }
    }
    fun resetBuild() {
        _selectedComponents.value = emptyMap()
    }

    fun saveBuild(context: Context) {
        val componentIds = _selectedComponents.value.values.mapNotNull { it.id }
        println("POST: $componentIds")

        viewModelScope.launch {
            try {
                val response = api.createBuild(BuildCreateRequest(componentIds))
                if (response.isSuccessful) {
                    val build = response.body()
                    println("✅ Build creada correctamente: $build")

                    build?.let {
                        // 🟢 Guarda la nueva ID acumulando las existentes
                        val sharedPrefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                        val existing = sharedPrefs.getStringSet("saved_build_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
                        existing.add(it.id.toString())
                        sharedPrefs.edit().putStringSet("saved_build_ids", existing).apply()

                        println("📌 IDs guardadas ahora: $existing")
                    }

                } else {
                    println("❌ Error al crear build: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                println("❌ Excepción al crear build: ${e.localizedMessage}")
            } finally {
                _selectedComponents.value = emptyMap()
            }
        }
    }

    private fun saveBuildIdToPrefs(context: Context, buildId: Long) {
        val sharedPrefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putLong("latest_build_id", buildId).apply()
        println("💾 ID guardado en SharedPreferences: $buildId")
    }

    fun deleteBuild(id: Long) {
        viewModelScope.launch {
            try {
                val response = api.deleteBuild(id)
                if (response.isSuccessful) {
                    println("Build eliminada: $id")
                } else {
                    println("Error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                println("Error eliminando build: ${e.message}")
            }
        }
    }

    fun getBuilds() {
        viewModelScope.launch {
            try {
                val response = api.getBuilds()
                if (response.isSuccessful) {
                    println("Builds: ${response.body()}")
                } else {
                    println("Error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                println("Error obteniendo builds: ${e.message}")
            }
        }
    }

    fun getBuildById(id: Long) {
        viewModelScope.launch {
            try {
                val response = api.getBuildById(id)
                if (response.isSuccessful) {
                    println("Build: ${response.body()}")
                } else {
                    println("Error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                println("Error obteniendo build: ${e.message}")
            }
        }
    }

    fun getBuildResult(id: Long) {
        viewModelScope.launch {
            try {
                val response = api.getBuildResult(id)
                if (response.isSuccessful) {
                    println("Resultado: ${response.body()}")
                } else {
                    println("Error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                println("Error obteniendo resultado: ${e.message}")
            }
        }
    }

    // ----------------------------------------------------
    // ✅ Utilidades
    // ----------------------------------------------------

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

@Serializable
data class Category(
    val id: Long,
    val name: String,
    val parent: Category? = null
)

data class Manufacturer(
    val id: Long,
    val name: String,
    val website: String? = null,
    val supportEmail: String? = null
)
