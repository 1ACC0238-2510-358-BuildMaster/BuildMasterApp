package com.buildmasterapp.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.login.data.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: LoginRepository) : ViewModel() {
    var token by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            runCatching { repo.login(email, password) }
                .onSuccess { token = it }
                .onFailure { errorMessage = "Login fallido" }
        }
    }

    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            runCatching { repo.register(email, password, nombre) }
                .onFailure { errorMessage = "Registro fallido" }
        }
    }
}