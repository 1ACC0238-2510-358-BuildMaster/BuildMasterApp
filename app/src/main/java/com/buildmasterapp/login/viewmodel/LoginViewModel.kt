package com.buildmasterapp.login.viewmodel

import android.accounts.NetworkErrorException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.login.data.api.LoginApi
import com.buildmasterapp.login.data.model.LoginRequest
import com.buildmasterapp.login.data.model.RegisterRequest
import kotlinx.coroutines.launch
import java.util.UUID

class LoginViewModel(private val repo: LoginApi) : ViewModel() {
    var token by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val result = repo.login(request)
                token = result.token // Assuming TokenResponse has a 'token' property
                errorMessage = null // Clear any previous error
            } catch (e: IllegalArgumentException) {
                errorMessage = "Invalid email or password"
            } catch (e: NetworkErrorException) {
                errorMessage = "Network error, please try again"
            } catch (e: Exception) {
                errorMessage = "Unexpected error: ${e.localizedMessage}"
            }
        }
    }

    fun register(email: String, password: String, nombre: String, biografy: String?, fotoUrl: String?) {
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    id = UUID.randomUUID(),
                    email = email,
                    passwordHash = password,
                    name = nombre,
                    biografy = biografy,
                    fotoUrl = fotoUrl
                )
                repo.register(request)
                errorMessage = null // Clear any previous error
            } catch (e: Exception) {
                errorMessage = "Registration failed"
            }
        }
    }
    

}