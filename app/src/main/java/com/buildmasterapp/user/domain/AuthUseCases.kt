package com.buildmasterapp.user.domain

import com.buildmasterapp.user.data.AuthApiService
import com.buildmasterapp.user.data.LoginRequest
import com.buildmasterapp.user.data.RegisterRequest
import com.buildmasterapp.user.data.RegisterResponse
import com.buildmasterapp.user.data.LoginResponse

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, email: String, password: String): RegisterResponse {
        return repository.register(username, email, password)
    }
}

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): LoginResponse {
        return repository.login(email, password)
    }
}
