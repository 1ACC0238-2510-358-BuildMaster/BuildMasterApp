package com.buildmasterapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel // Necesario para viewModel()
import com.buildmasterapp.community.ui.CommunityScreen // Importa la pantalla principal de la comunidad
import com.buildmasterapp.community.viewmodel.CommunityViewModel // Importa el ViewModel de la comunidad

@Composable
fun ChatScreen() { // Esta función es la que se llama desde tu AppNavHost para la ruta "chat"
    // Instanciamos el ViewModel directamente aquí
    val communityViewModel: CommunityViewModel = viewModel()
    // Y llamamos a la pantalla principal de la comunidad, pasándole el ViewModel
    CommunityScreen(communityViewModel = communityViewModel)
}