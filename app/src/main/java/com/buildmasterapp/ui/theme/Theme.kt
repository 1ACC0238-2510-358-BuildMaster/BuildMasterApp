package com.buildmasterapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val BuildMasterDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00B253),       // Verde Tecla
    secondary = Color(0xFF333333),     // Gris Oscuro
    tertiary = Color(0xFFF5F5F5),      // Gris Claro
    background = Color(0xFF000000),    // Negro si es dark theme
    surface = Color(0xFF333333),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black
)

private val BuildMasterLightColorScheme = lightColorScheme(
    primary = Color(0xFF00B253),       // Verde Tecla
    secondary = Color(0xFF333333),     // Gris Oscuro
    tertiary = Color(0xFFF5F5F5),      // Gris Claro
    background = Color(0xFFFFFFFF),    // Blanco
    surface = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black
)

@Composable
fun BuildMasterAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> BuildMasterDarkColorScheme
        else -> BuildMasterLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BuildMasterTypography,
        content = content
    )
}