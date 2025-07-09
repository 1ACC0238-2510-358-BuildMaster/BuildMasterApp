package com.buildmasterapp.user.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import com.buildmasterapp.user.data.ProfileApi
import com.buildmasterapp.user.data.ProfileRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización manual de Retrofit y dependencias
        val retrofit = Retrofit.Builder()
            .baseUrl("https://buildmaster-api-ddh3asdah2bsggfs.canadacentral-01.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ProfileApi::class.java)
        val repository = ProfileRepository(api)
        val viewModel = ProfileViewModel(repository)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            LaunchedEffect(Unit) {
                viewModel.loadProfile(4)
            }
            ProfileScreen(
                uiState = uiState,
                onRetry = { viewModel.loadProfile(4) }
            )
        }
    }
}

