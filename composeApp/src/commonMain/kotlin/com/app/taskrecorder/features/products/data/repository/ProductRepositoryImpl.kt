package com.app.taskrecorder.features.products.data.repository

import com.app.taskrecorder.core.network.safeApiCall
import com.app.taskrecorder.core.util.Resource
import com.app.taskrecorder.features.products.data.mapper.toDomain
import com.app.taskrecorder.features.products.data.remote.ProductApi
import com.app.taskrecorder.features.products.data.remote.dto.ProductDto
import com.app.taskrecorder.features.products.data.remote.dto.ProductsResponseDto
import com.app.taskrecorder.features.products.domain.model.Product
import com.app.taskrecorder.features.products.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val api: ProductApi
) : ProductRepository {
    
    override suspend fun getProducts(): Resource<List<Product>> {
        return when (val result = safeApiCall<ProductsResponseDto> { api.getProducts() }) {
            is Resource.Success -> Resource.Success(result.data.products.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.exception)
        }
    }
    
    override suspend fun getProduct(id: Int): Resource<Product> {
        return when (val result = safeApiCall<ProductDto> { api.getProduct(id) }) {
            is Resource.Success -> Resource.Success(result.data.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.exception)
        }
    }
}
