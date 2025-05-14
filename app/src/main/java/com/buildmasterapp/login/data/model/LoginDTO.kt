package com.buildmasterapp.login.data.model

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val nombre: String)
data class TokenResponse(val token: String)