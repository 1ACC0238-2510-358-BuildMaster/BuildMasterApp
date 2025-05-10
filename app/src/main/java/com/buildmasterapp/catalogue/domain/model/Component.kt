package com.buildmasterapp.catalogue.domain.model

import com.buildmasterapp.catalogue.domain.model.Specifications

data class Component(
    val id: Long? = null,
    val name: String,
    val type: String,
    val price: Double,
    val specifications: Specifications,
    val categoryId: Long,
    val manufacturerId: Long
)