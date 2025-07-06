package com.buildmasterapp.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buildmasterapp.user.domain.LoginUseCase
import com.buildmasterapp.user.domain.RegisterUseCase
import com.buildmasterapp.user.data.DataStoreManager

class AuthViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(loginUseCase, registerUseCase, dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

