package com.app.taskrecorder.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Start : Screen()
    
    @Serializable
    data object NoiseTest : Screen()
    
    @Serializable
    data object TaskSelection : Screen()
    
    @Serializable
    data object TextReading : Screen()
    
    @Serializable
    data object ImageDescription : Screen()
    
    @Serializable
    data object PhotoCapture : Screen()
    
    @Serializable
    data object TaskHistory : Screen()
}
