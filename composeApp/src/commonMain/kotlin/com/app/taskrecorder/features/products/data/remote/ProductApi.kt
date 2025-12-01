package com.app.taskrecorder.features.products.data.remote

import com.app.taskrecorder.core.config.ApiConfig
import com.app.taskrecorder.features.products.data.remote.dto.ProductDto
import com.app.taskrecorder.features.products.data.remote.dto.ProductsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class ProductApi(private val client: HttpClient) {
    suspend fun getProducts(): HttpResponse {
        return client.get("${ApiConfig.BASE_URL}${ApiConfig.Routes.PRODUCTS}")
    }
    
    suspend fun getProduct(id: Int): HttpResponse {
        return client.get("${ApiConfig.BASE_URL}${ApiConfig.Routes.getProduct(id)}")
    }
}
