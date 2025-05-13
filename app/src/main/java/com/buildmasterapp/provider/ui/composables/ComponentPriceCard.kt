package com.buildmasterapp.provider.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildmasterapp.provider.data.model.ComponentPriceInfo
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.text.toDouble


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentPriceCard(
    componentInfo: ComponentPriceInfo,
    onRefreshClicked: (String) -> Unit
) {
    val currencyFormat = (NumberFormat.getCurrencyInstance(Locale.GERMANY) as java.text.DecimalFormat).apply {
        val symbols = decimalFormatSymbols
        symbols.currencySymbol = componentInfo.currencySymbol
        decimalFormatSymbols = symbols
        if (componentInfo.averagePrice != null && componentInfo.averagePrice == componentInfo.averagePrice.roundToInt().toDouble()) {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        } else {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = componentInfo.iconRes),
                    contentDescription = componentInfo.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = componentInfo.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (componentInfo.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Precio Promedio:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = componentInfo.averagePrice?.let { currencyFormat.format(it) } ?: "Calculando...",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Rango: ${componentInfo.priceRange ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    FilledTonalButton(onClick = { onRefreshClicked(componentInfo.id) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar precios")
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Actualizar")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Última actualización: ${formatTimestamp(componentInfo.lastUpdated)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    val sdf = java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}