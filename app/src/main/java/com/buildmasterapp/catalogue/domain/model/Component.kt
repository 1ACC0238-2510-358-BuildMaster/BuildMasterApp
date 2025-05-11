package com.buildmasterapp.catalogue.domain.model

import com.buildmasterapp.catalogue.domain.model.Specifications
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Component(
    val id: Long? = null,
    val name: String,
    val type: String,
    val price: Double,
    val specifications: Specifications,
    val category: Category,
    val manufacturer: Manufacturer
)
@Serializable
data class Category(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("parent") val parent: Category? = null
)

data class Manufacturer(
    val id: Long,
    val name: String,
    val website: String? = null,
    val supportEmail: String? = null
)