package com.buildmasterapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.buildmasterapp.ui.composables.SettingsScreen
import com.buildmasterapp.ui.theme.BuildMasterAppTheme
import com.buildmasterapp.ui.theme.ThemeViewModel
import com.buildmasterapp.ui.theme.ThemeViewModelFactory

class SettingsFragment : AppCompatActivity() {

    private lateinit var themeViewModel: ThemeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val factory = ThemeViewModelFactory(sharedPreferences)
        themeViewModel = ViewModelProvider(this, factory)[ThemeViewModel::class.java]

        setContent {
            val theme by themeViewModel.theme
            BuildMasterAppTheme(
                darkTheme = when (theme) {
                    "light" -> false
                    "dark" -> true
                    else -> isSystemInDarkTheme()
                }
            ) {
                SettingsScreen(onLogout = { finish() })
            }
        }
    }
}
