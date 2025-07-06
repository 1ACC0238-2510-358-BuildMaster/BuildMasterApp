package com.buildmasterapp.user.domain

import com.buildmasterapp.user.data.AuthApiService
import com.buildmasterapp.user.data.LoginRequest
import com.buildmasterapp.user.data.RegisterRequest
import com.buildmasterapp.user.data.RegisterResponse
import com.buildmasterapp.user.data.LoginResponse


class AuthRepository(private val api: AuthApiService) {
    suspend fun register(username: String, email: String, password: String, role: String = "USER"): RegisterResponse {
        return api.register(RegisterRequest(username, email, password, role))
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return api.login(LoginRequest(email, password))
    }
}
