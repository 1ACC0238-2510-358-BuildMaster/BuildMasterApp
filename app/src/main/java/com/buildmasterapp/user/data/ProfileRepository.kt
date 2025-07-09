package com.buildmasterapp.user.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository(private val api: ProfileApi) {
    suspend fun getProfile(userId: Int): Result<UserProfileDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val profile = api.getProfile(userId)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<UserProfileDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val me = api.getMe() // Llama a /users/me y obtiene el id
            val profile = api.getProfile(me.id) // Llama a /profiles/{id} y obtiene el perfil completo
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
