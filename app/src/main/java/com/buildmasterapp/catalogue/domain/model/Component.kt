package com.buildmasterapp.catalogue.domain.model

import com.buildmasterapp.catalogue.domain.model.Specifications

data class Component(
    val id: Long? = null,
    val name: String,
    val type: String,
    val price: Double,
    val specifications: Specifications,
    val category: Category,
    val manufacturer: Manufacturer
)
data class Category(
    val id: Long,
    val name: String,
    val parent: Category? = null
)

data class Manufacturer(
    val id: Long,
    val name: String,
    val website: String? = null,
    val supportEmail: String? = null
)