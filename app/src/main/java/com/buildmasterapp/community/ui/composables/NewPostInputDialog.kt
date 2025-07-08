package com.buildmasterapp.community.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String, String, List<String>) -> Unit
) {
    var postTitle by remember { mutableStateOf("") }
    var postContent by remember { mutableStateOf("") }
    var mediaUrls by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Crear Nuevo Post", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = postTitle,
                    onValueChange = { postTitle = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = mediaUrls,
                    onValueChange = { mediaUrls = it },
                    label = { Text("URLs de medios (separados por comas)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val mediaList = mediaUrls.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            onConfirm(postTitle, postContent, mediaList)
                        },
                        enabled = postTitle.isNotBlank() && postContent.isNotBlank()
                    ) {
                        Text("Postear")
                    }
                }
            }
        }
    }
}