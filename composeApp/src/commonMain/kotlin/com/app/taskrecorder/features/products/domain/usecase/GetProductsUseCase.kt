package com.app.taskrecorder.features.products.domain.usecase

import com.app.taskrecorder.core.util.Resource
import com.app.taskrecorder.features.products.domain.model.Product
import com.app.taskrecorder.features.products.domain.repository.ProductRepository

class GetProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): Resource<List<Product>> {
        return repository.getProducts()
    }
}
