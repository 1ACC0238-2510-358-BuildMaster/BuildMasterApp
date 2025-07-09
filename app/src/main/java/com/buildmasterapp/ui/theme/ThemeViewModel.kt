package com.buildmasterapp.ui.theme

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ThemeViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    val theme = mutableStateOf(sharedPreferences.getString("theme", "system") ?: "system")

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == "theme") {
            theme.value = prefs.getString("theme", "system") ?: "system"
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}

class ThemeViewModelFactory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

