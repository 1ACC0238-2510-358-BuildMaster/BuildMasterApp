package com.buildmasterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.buildmasterapp.catalogue.data.api.RetrofitClient
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModelFactory
import com.buildmasterapp.shared.navigation.Navigator
import com.buildmasterapp.ui.theme.BuildMasterTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ComponentViewModel by viewModels {
        ComponentViewModelFactory(RetrofitClient.instance)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BuildMasterTheme {
                Navigator(viewModel = viewModel, context = this)
            }
        }
    }
}
