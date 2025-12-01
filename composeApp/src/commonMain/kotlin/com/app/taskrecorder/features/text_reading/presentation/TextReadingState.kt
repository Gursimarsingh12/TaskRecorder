package com.app.taskrecorder.features.text_reading.presentation

import com.app.taskrecorder.features.products.domain.model.Product

data class TextReadingState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val noBackgroundNoise: Boolean = false,
    val noMistakes: Boolean = false,
    val noErrorsInMiddle: Boolean = false,
    val hasPermission: Boolean = false,
    val isSubmitting: Boolean = false
)