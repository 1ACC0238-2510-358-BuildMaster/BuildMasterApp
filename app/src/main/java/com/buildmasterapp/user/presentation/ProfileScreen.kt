package com.buildmasterapp.user.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.buildmasterapp.user.data.UserProfileDto

// Datos mockeados para previsualización
private val mockProfile = UserProfile(
    firstName = "Nate",
    lastName = "Gentile",
    profilePictureUrl = "https://yt3.googleusercontent.com/bsvlD3rLbbYBhHF75p4BugJ2Z6ZcOU5flR7EY9W304sGomOcUlhipZoVg7SfhpLJV0RGatJlig=s900-c-k-c0x00ffffff-no-rj",
    description = "Configurador de pc profesional",
    age = 34,
    phoneNumber = "+1 8796549"
)

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String,
    val description: String,
    val age: Int,
    val phoneNumber: String
)

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onRetry: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF007AFF)
                )
            }
            is ProfileUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${uiState.message}",
                        color = Color.Red,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (onRetry != null) {
                        Button(onClick = onRetry) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is ProfileUiState.Success -> {
                ProfileContent(userProfile = uiState.profile, onLogout = onLogout)
            }
        }
    }
}

@Composable
private fun ProfileContent(userProfile: UserProfileDto, onLogout: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        // Imagen de perfil
        Image(
            painter = rememberAsyncImagePainter(userProfile.profilePictureUrl ?: ""),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Nombre
        Text(
            text = "${userProfile.firstName?.trim() ?: ""} ${userProfile.lastName ?: ""}",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Descripción breve
        Text(
            text = userProfile.description ?: "",
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = Color(0xFF8E8E93), // Gris claro estilo Apple
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Detalles adicionales
        Text(
            text = "Edad: ${userProfile.age} | Teléfono: ${userProfile.phoneNumber ?: ""}",
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color(0xFF007AFF), // Azul acento Apple
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(36.dp))
        // Sección Acerca de
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Acerca de",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = userProfile.description ?: "",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color(0xFF222222),
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (onLogout != null) {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
