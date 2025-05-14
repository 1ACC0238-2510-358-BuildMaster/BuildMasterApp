package com.buildmasterapp.login.data.api

import com.buildmasterapp.login.data.model.LoginRequest
import com.buildmasterapp.login.data.model.RegisterRequest
import com.buildmasterapp.login.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("/api/login/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse
    @POST("/api/login/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
}