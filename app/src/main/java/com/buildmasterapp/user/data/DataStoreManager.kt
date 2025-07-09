package com.buildmasterapp.user.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey // Import this
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Declarar la extensión fuera de la clase y del companion object
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        // Use stringPreferencesKey for String type
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    fun getToken(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    fun getUserId(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }

    suspend fun clearUserId() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
        }
    }
}