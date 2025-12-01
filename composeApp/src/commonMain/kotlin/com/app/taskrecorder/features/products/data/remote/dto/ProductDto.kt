package com.app.taskrecorder.features.products.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double? = null,
    val discountPercentage: Double? = null,
    val rating: Double? = null,
    val stock: Int? = null,
    val brand: String? = null,
    val category: String? = null,
    val thumbnail: String,
    val images: List<String> = emptyList()
)

@Serializable
data class ProductsResponseDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
