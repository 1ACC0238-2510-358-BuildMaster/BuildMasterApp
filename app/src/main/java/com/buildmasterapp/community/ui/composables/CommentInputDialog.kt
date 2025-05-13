package com.buildmasterapp.community.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputDialog(
    postAuthor: String, // Para dar contexto en el título del diálogo
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit // Devuelve el texto del comentario
) {
    var commentText by remember { mutableStateOf("") }

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
                Text("Responder a ${postAuthor}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Escribe tu comentario...") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 160.dp), // Altura limitada
                    maxLines = 5,
                    singleLine = false
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
                            onConfirm(commentText)
                        },
                        enabled = commentText.isNotBlank()
                    ) {
                        Text("Comentar")
                    }
                }
            }
        }
    }
}