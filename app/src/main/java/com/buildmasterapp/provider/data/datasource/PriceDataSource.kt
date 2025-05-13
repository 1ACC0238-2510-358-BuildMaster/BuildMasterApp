package com.buildmasterapp.provider.data.datasource

interface PriceDataSource {
    suspend fun fetchComponentPrices(componentId: String): List<Double>
}