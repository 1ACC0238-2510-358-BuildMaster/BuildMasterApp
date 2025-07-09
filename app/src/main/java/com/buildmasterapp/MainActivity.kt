package com.buildmasterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import com.buildmasterapp.ui.composables.DashboardScreen
import com.buildmasterapp.ui.theme.BuildMasterAppTheme
import com.buildmasterapp.ui.theme.ThemeViewModel
import com.buildmasterapp.ui.theme.ThemeViewModelFactory
import com.buildmasterapp.user.presentation.AuthNavHost

class MainActivity : ComponentActivity() {

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthNavHost()
                }
            }
        }
    }
}
