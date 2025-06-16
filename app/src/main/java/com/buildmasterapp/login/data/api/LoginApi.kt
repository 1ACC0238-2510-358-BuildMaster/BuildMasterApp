package com.buildmasterapp.login.data.api

import com.buildmasterapp.login.data.model.LoginRequest
import com.buildmasterapp.login.data.model.RegisterRequest
import com.buildmasterapp.login.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApi {
    @GET("/api/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse
    @POST("/api/login")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
}