package com.buildmasterapp.provider.data.repository

import com.buildmasterapp.provider.data.datasource.PriceDataSource
import com.buildmasterapp.provider.data.model.ComponentPriceInfo
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.text.toDouble


class PriceRepository(private val dataSource: PriceDataSource) {

    suspend fun getProcessedComponentPriceInfo(initialInfo: ComponentPriceInfo): ComponentPriceInfo {
        val rawPrices = dataSource.fetchComponentPrices(initialInfo.id)
        if (rawPrices.isEmpty()) {
            return initialInfo.copy(
                allPrices = emptyList(),
                averagePrice = null,
                priceRange = "No disponible",
                lastUpdated = System.currentTimeMillis()
            )
        }

        val pricesWithoutOutliers = removeOutliersIQR(rawPrices)
        val averagePrice =
            if (pricesWithoutOutliers.isNotEmpty()) pricesWithoutOutliers.average() else null
        val priceRange = if (rawPrices.isNotEmpty()) {
            "${formatCurrency(rawPrices.minOrNull())} - ${formatCurrency(rawPrices.maxOrNull())}"
        } else null

        return initialInfo.copy(
            allPrices = rawPrices,
            averagePrice = averagePrice,
            priceRange = priceRange,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun removeOutliersIQR(prices: List<Double>): List<Double> {
        if (prices.size < 4) return prices

        val sortedPrices = prices.sorted()
        val q1 = percentile(sortedPrices, 25.0)
        val q3 = percentile(sortedPrices, 75.0)
        val iqr = q3 - q1

        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr

        return sortedPrices.filter { it >= lowerBound && it <= upperBound }
    }

    private fun percentile(sortedData: List<Double>, percentile: Double): Double {
        if (sortedData.isEmpty()) return 0.0
        val index = (percentile / 100.0) * (sortedData.size - 1)
        return if (index % 1 == 0.0) {
            sortedData[index.toInt()]
        } else {
            val lower = sortedData[index.toInt()]
            val upper = sortedData[index.toInt() + 1]
            lower + (index - index.toInt()) * (upper - lower)
        }
    }

    private fun formatCurrency(value: Double?, symbol: String = "€"): String {
        return value?.let {
            val format = NumberFormat.getCurrencyInstance(Locale.GERMANY) as java.text.DecimalFormat
            val symbols = format.decimalFormatSymbols
            symbols.currencySymbol = symbol
            format.decimalFormatSymbols = symbols

            if (it == it.roundToInt().toDouble()) {
                format.minimumFractionDigits = 0
                format.maximumFractionDigits = 0
            } else {
                format.minimumFractionDigits = 2
                format.maximumFractionDigits = 2
            }
            format.format(it)
        } ?: "N/A"
    }

}