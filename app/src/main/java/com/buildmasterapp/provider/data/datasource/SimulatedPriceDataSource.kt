package com.buildmasterapp.provider.data.datasource

import com.buildmasterapp.provider.data.model.ComponentPriceInfo
import kotlinx.coroutines.delay
import kotlin.random.Random

class SimulatedPriceDataSource : PriceDataSource {
    override suspend fun fetchComponentPrices(componentId: String): List<Double> {
        // Simula una llamada de red
        delay(Random.nextLong(500, 2000))

        // Simulación de datos de www.pccomponentes.com
        return when (componentId) {
            ComponentPriceInfo.ID_RTX_4060 -> listOf(
                339.90, 345.50, 352.00, 335.75, 349.99, 360.00, 342.50,
                410.00, // Posible outlier alto
                290.00  // Posible outlier bajo
            ).shuffled().take(Random.nextInt(5,9)) // Simula variabilidad
            ComponentPriceInfo.ID_RYZEN_5800X -> listOf(
                210.90, 215.00, 205.50, 220.75, 212.99,
                280.00, // Posible outlier alto
                170.00  // Posible outlier bajo
            ).shuffled().take(Random.nextInt(4,7))
            ComponentPriceInfo.ID_RAM_DDR5_32GB -> listOf(
                110.0, 115.50, 122.00, 105.75, 119.99, 130.00,
                160.00, // Posible outlier
                90.00   // Posible outlier
            ).shuffled().take(Random.nextInt(5,8))
            else -> emptyList()
        }
    }
}