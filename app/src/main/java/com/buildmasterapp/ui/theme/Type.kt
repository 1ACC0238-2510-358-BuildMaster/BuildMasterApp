package com.buildmasterapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val BuildMasterTypography = Typography(
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Color.Black
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        color = Color(0xFF333333) // Gris Oscuro
    )
)
