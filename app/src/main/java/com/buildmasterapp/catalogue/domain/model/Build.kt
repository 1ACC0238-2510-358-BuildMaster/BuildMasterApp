package com.buildmasterapp.catalogue.domain.model

data class Build(
    val id: Long,
    val componentIds: List<Long>,
    val createdAt: String
)