package com.app.taskrecorder.features.products.domain.usecase

import com.app.taskrecorder.core.util.Resource
import com.app.taskrecorder.features.products.domain.model.Product
import com.app.taskrecorder.features.products.domain.repository.ProductRepository

class GetProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Int): Resource<Product> {
        return repository.getProduct(id)
    }
}
