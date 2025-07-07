package com.buildmasterapp.catalogue.domain.model

data class BuildResult(
    val buildId: Long,
    val estimatedPerformance: String,
    val powerConsumptionWatts: Int,
    val estimatedPrice: Double,
    val observations: String
)