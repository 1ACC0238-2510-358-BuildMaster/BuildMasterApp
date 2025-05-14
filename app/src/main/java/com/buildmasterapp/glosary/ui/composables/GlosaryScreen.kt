package com.buildmasterapp.glosary.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.buildmasterapp.glosary.viewmodel.GlosaryViewModel

@Composable
fun GlosaryScreen(viewModel: GlosaryViewModel) {
    val termino = viewModel.termino
    val guia = viewModel.guia
    val tip = viewModel.tip

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { viewModel.search("gpu") }) {
            Text("Buscar término GPU")
        }
        termino?.let {
            Text("Término: ${it.termino}")
            Text("Definición: ${it.definicion}")
            it.ejemplos.forEach { ejemplo -> Text("Ejemplo: $ejemplo") }
        }

        Button(onClick = { viewModel.loadGuide("GPU") }) {
            Text("Ver guía GPU")
        }
        guia?.let {
            Text("Guía ${it.categoria}: ${it.contenido}")
        }

        Button(onClick = { viewModel.loadTip("GPU") }) {
            Text("Ver Tip para paso: GPU")
        }
        tip?.let {
            Text("Tip: ${it.mensaje}")
        }
    }
}


