package com.app.taskrecorder

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.app.taskrecorder.navigation.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}
