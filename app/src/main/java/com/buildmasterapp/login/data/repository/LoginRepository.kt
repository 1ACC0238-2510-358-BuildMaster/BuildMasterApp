package com.buildmasterapp.login.data.repository

import com.buildmasterapp.login.data.api.LoginApi
import com.buildmasterapp.login.data.model.LoginRequest
import com.buildmasterapp.login.data.model.RegisterRequest

class LoginRepository(private val api: LoginApi) {
    suspend fun login(email: String, password: String): String =
        api.login(LoginRequest(email, password)).token

    suspend fun register(email: String, password: String, nombre: String) {
        api.register(RegisterRequest(email, password, nombre))
    }
}