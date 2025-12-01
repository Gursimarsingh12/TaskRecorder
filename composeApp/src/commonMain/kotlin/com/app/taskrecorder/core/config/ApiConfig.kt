package com.app.taskrecorder.core.config

object ApiConfig {
    const val BASE_URL = "https://dummyjson.com"
    
    object Routes {
        const val PRODUCTS = "/products"
        
        fun getProduct(id: Int) = "$PRODUCTS/$id"
    }
}
