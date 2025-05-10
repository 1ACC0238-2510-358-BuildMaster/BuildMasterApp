package com.buildmasterapp.catalogue.domain.model

data class Specifications(
    val socket: String,
    val memoryType: String,
    val powerConsumptionWatts: Int,
    val formFactor: String
)