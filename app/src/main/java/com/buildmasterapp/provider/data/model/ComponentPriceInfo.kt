package com.buildmasterapp.provider.data.model

import androidx.annotation.DrawableRes

import com.buildmasterapp.R

enum class ComponentType {
    GPU, CPU, RAM, MOTHERBOARD // Puedes añadir más
}

data class ComponentPriceInfo(
    val id: String, // e.g., "rtx4060"
    val name: String, // e.g., "NVIDIA RTX 4060"
    @DrawableRes val iconRes: Int, // Placeholder para ícono local
    val type: ComponentType,
    val allPrices: List<Double> = emptyList(),
    val averagePrice: Double? = null,
    val priceRange: String? = null, // e.g., "345€ - 419€"
    val currencySymbol: String = "€",
    val isLoading: Boolean = false,
    val lastUpdated: Long = 0L // Timestamp
) {
    companion object {
        // Valores de ejemplo / Identificadores
        const val ID_RTX_4060 = "rtx4060"
        const val ID_RYZEN_5800X = "ryzen5800x"
        const val ID_RAM_DDR5_32GB = "ram_ddr5_32gb"

        fun getInitialComponents(): List<ComponentPriceInfo> {
            return listOf(
                ComponentPriceInfo(
                    id = ID_RTX_4060,
                    name = "NVIDIA RTX 4060",
                    iconRes = R.drawable.ic_placeholder_gpu, // Necesitarás crear este drawable
                    type = ComponentType.GPU
                ),
                ComponentPriceInfo(
                    id = ID_RYZEN_5800X,
                    name = "AMD Ryzen 7 5800X",
                    iconRes = R.drawable.ic_placeholder_cpu, // Necesitarás crear este drawable
                    type = ComponentType.CPU
                ),
                ComponentPriceInfo(
                    id = ID_RAM_DDR5_32GB,
                    name = "RAM DDR5 32GB (2x16GB)",
                    iconRes = R.drawable.ic_placeholder_ram, // Necesitarás crear este drawable
                    type = ComponentType.RAM
                )
            )
        }
    }
}