package com.buildmasterapp.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buildmasterapp.login.data.api.LoginApi

class LoginViewModelFactory(private val api: LoginApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}