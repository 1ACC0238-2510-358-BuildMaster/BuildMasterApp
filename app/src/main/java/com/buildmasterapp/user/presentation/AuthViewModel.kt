package com.buildmasterapp.user.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.user.data.DataStoreManager
import com.buildmasterapp.user.domain.LoginUseCase
import com.buildmasterapp.user.domain.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Intentando login con email: $email")
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                Log.d("AuthViewModel", "Enviando request de login...")
                val response = loginUseCase(email, password)
                Log.d("AuthViewModel", "Respuesta login recibida: access_token=${response.access_token}")

                if (!response.access_token.isNullOrEmpty()) {
                    Log.d("AuthViewModel", "Login exitoso, guardando token...")
                    dataStoreManager.saveToken(response.access_token)
                    _uiState.value = AuthUiState.Success(email)
                } else {
                    Log.e("AuthViewModel", "Login fallido: token vacío o nulo")
                    _uiState.value = AuthUiState.Error("Credenciales incorrectas")
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("AuthViewModel", "Error HTTP ${e.code()} en login: $errorBody", e)
                _uiState.value = AuthUiState.Error("Error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en login", e)
                _uiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        Log.d("AuthViewModel", "Intentando registro con usuario: $username, email: $email")
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                Log.d("AuthViewModel", "Enviando request de registro...")
                val response = registerUseCase(username, email, password)
                Log.d("AuthViewModel", "Respuesta registro recibida: id=${response.id}, username=${response.username}")

                if (response.id > 0 && response.username.isNotEmpty()) {
                    Log.d("AuthViewModel", "Registro exitoso")
                    _uiState.value = AuthUiState.Success(response.username)
                } else {
                    Log.e("AuthViewModel", "Registro fallido: respuesta inválida")
                    _uiState.value = AuthUiState.Error("No se pudo registrar el usuario")
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("AuthViewModel", "Error HTTP ${e.code()} en registro: $errorBody", e)
                _uiState.value = AuthUiState.Error("Error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en registro", e)
                _uiState.value = AuthUiState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
