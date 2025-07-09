package com.buildmasterapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.buildmasterapp.R
import com.buildmasterapp.ui.composables.SettingsScreen

class SettingsFragment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen(onLogout = { finish() })
        }
    }
}
