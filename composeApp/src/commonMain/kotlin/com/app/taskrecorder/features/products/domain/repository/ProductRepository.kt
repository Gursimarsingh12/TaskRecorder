package com.app.taskrecorder.features.products.domain.repository

import com.app.taskrecorder.core.util.Resource
import com.app.taskrecorder.features.products.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): Resource<List<Product>>
    suspend fun getProduct(id: Int): Resource<Product>
}
