package com.buildmasterapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.buildmasterapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@Composable
fun GlosaryScreen() {
    var glossary by remember { mutableStateOf<List<Map<String, String>>?>(null) }
    var tips by remember { mutableStateOf<Map<String, List<String>>?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                URL("https://buildmaster-api-ddh3asdah2bsggfs.canadacentral-01.azurewebsites.net/glosario").readText()
            }
            val json = JSONObject(response)
            val glosario = json.getJSONArray("glosario_facil")
            val consejos = json.getJSONObject("consejos_utiles")
            val glossaryList = (0 until glosario.length()).map { i ->
                val obj = glosario.getJSONObject(i)
                obj.keys().asSequence().associateWith { obj.getString(it) }
            }
            val tipsMap = consejos.keys().asSequence().associateWith { key ->
                val arr = consejos.getJSONArray(key)
                List(arr.length()) { arr.getString(it) }
            }
            glossary = glossaryList
            tips = tipsMap
            loading = false
        } catch (e: Exception) {
            error = true
            errorMessage = e.localizedMessage
            loading = false
        }
    }

    Scaffold(
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                error -> Text("Error al cargar el glosario: ${errorMessage ?: "desconocido"}", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
                glossary.isNullOrEmpty() -> Text(stringResource(R.string.glossary_empty), modifier = Modifier.padding(16.dp))
                else -> LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(glossary!!) { item ->
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(item["palabra"] ?: "", style = MaterialTheme.typography.titleMedium)
                                Text(item["explicacion"] ?: "", style = MaterialTheme.typography.bodyMedium)
                                item["ejemplo"]?.let {
                                    Text("Ejemplo: $it", style = MaterialTheme.typography.bodySmall)
                                }
                                item["tip"]?.let {
                                    Text("Tip: $it", style = MaterialTheme.typography.bodySmall)
                                }
                                item["warning"]?.let {
                                    Text("⚠ $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Consejos útiles:", style = MaterialTheme.typography.titleMedium)
                        tips?.forEach { (key, list) ->
                            Text("- $key:", style = MaterialTheme.typography.bodyMedium)
                            list.forEach { tip ->
                                Text("   • $tip", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
