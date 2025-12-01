package com.app.taskrecorder.core.network

import com.app.taskrecorder.core.util.Resource
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException

suspend inline fun <reified T> safeApiCall(
    apiCall: () -> HttpResponse
): Resource<T> {
    return try {
        val response = apiCall()
        if (response.status.value in 200..299) {
            Resource.Success(response.body<T>())
        } else {
            Resource.Error("HTTP ${response.status.value}: ${response.status.description}")
        }
    } catch (e: IOException) {
        Resource.Error("Network error: ${e.message}", e)
    } catch (e: Exception) {
        Resource.Error("Unexpected error: ${e.message}", e)
    }
}
