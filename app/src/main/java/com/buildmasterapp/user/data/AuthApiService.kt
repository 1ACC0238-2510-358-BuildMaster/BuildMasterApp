package com.buildmasterapp.user.data

import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val username: String, val email: String, val password: String, val role: String)
data class RegisterResponse(val id: Int, val username: String)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val access_token: String)

interface AuthApiService {
    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
