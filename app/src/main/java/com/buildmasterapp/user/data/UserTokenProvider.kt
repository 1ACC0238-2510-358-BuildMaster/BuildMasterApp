package com.buildmasterapp.user.data

import android.content.Context
import com.buildmasterapp.community.data.TokenProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UserTokenProvider(private val context: Context) : TokenProvider {
    private val dataStoreManager = DataStoreManager(context)

    override fun getToken(): String {
        // Bloquea hasta obtener el token actual (puedes adaptar esto a tu flujo de trabajo)
        return runBlocking {
            dataStoreManager.getToken().first() ?: ""
        }
    }
}

