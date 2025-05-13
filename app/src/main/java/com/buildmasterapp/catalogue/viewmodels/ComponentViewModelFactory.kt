package com.buildmasterapp.catalogue.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buildmasterapp.catalogue.data.api.ComponentApi

class ComponentViewModelFactory(private val api: ComponentApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComponentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComponentViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}