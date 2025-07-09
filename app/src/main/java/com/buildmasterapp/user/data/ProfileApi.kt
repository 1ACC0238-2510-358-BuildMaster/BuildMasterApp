package com.buildmasterapp.user.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {
    @GET("profiles/{userId}")
    suspend fun getProfile(@Path("userId") userId: Int): UserProfileDto

    @GET("users/me")
    suspend fun getMe(): UserMeDto
}
