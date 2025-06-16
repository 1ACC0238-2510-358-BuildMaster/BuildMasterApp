package com.buildmasterapp.login.data.model

import java.util.UUID

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val id: UUID, val email: String, val passwordHash: String, val name: String, val biografy: String?, val fotoUrl: String?)
data class TokenResponse(val token: String)