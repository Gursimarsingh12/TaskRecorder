package com.app.taskrecorder.features.products.data.mapper

import com.app.taskrecorder.features.products.data.remote.dto.ProductDto
import com.app.taskrecorder.features.products.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        imageUrl = thumbnail,
        images = images
    )
}

fun List<ProductDto>.toDomain(): List<Product> {
    return map { it.toDomain() }
}
