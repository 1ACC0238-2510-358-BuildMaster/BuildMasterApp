package com.buildmasterapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.buildmasterapp.R // Asegúrate de que este es el R correcto de tu proyecto
import com.buildmasterapp.ui.theme.BuildMasterTheme // Asegúrate de que este es tu tema

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen() {
    var titleVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }
    var iconScale by remember { mutableFloatStateOf(1f) }

    val animatedIconScale by animateFloatAsState(
        targetValue = iconScale,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        titleVisible = true
        kotlinx.coroutines.delay(300)
        taglineVisible = true
        kotlinx.coroutines.delay(400)
        buttonVisible = true
        iconScale = 1.2f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large
                )
                .padding(24.dp)
                .widthIn(max = 600.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = "PC Icon",
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer(scaleX = animatedIconScale, scaleY = animatedIconScale),
                tint = MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            AnimatedVisibility(
                visible = taglineVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 200)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Text(
                    text = "Tu Socio para Construir la PC Perfecta",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "Diseñamos y ensamblamos equipos de cómputo personalizados para empresas y familias, optimizados para tus necesidades específicas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Video de YouTube con reproducción automática

            AndroidView(
                factory = { context ->
                    android.widget.FrameLayout(context).apply {
                        val webView = android.webkit.WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.mediaPlaybackRequiresUserGesture = false // Permitir reproducción automática
                            loadUrl("https://www.youtube.com/embed/avlpSYSDB5Q?si=oI2nBHBTu-Sizvsw")
                        }
                        addView(webView)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de acción
            AnimatedVisibility(
                visible = buttonVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 400)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Button(
                    onClick = { /* Acción del botón */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Comenzar Configuración",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Íconos adicionales
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = "Icono de Computadora",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = "Icono de Ensamblaje",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(40.dp)
                )
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = "Icono de Soporte",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}