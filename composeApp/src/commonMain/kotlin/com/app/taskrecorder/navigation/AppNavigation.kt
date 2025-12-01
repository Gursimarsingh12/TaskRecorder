package com.app.taskrecorder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.taskrecorder.features.image_description.presentation.ImageDescriptionScreen
import com.app.taskrecorder.features.noise_test.presentation.NoiseTestScreen
import com.app.taskrecorder.features.photo_capture.presentation.PhotoCaptureScreen
import com.app.taskrecorder.features.start.presentation.StartScreen
import com.app.taskrecorder.features.task_history.presentation.TaskHistoryScreen
import com.app.taskrecorder.features.task_selection.presentation.TaskSelectionScreen
import com.app.taskrecorder.features.text_reading.presentation.TextReadingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Start,
        enterTransition = { NavAnimations.slideInFromRight() },
        exitTransition = { NavAnimations.slideOutToLeft() },
        popEnterTransition = { NavAnimations.slideInFromLeft() },
        popExitTransition = { NavAnimations.slideOutToRight() }
    ) {
        composable<Screen.Start> {
            StartScreen(
                onStartClick = {
                    navController.navigate(Screen.NoiseTest)
                }
            )
        }
        
        composable<Screen.NoiseTest> {
            NoiseTestScreen(
                onTestPassed = {
                    navController.navigate(Screen.TaskSelection)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Screen.TaskSelection> {
            TaskSelectionScreen(
                onTextReadingClick = {
                    navController.navigate(Screen.TextReading)
                },
                onImageDescriptionClick = {
                    navController.navigate(Screen.ImageDescription)
                },
                onPhotoCaptureClick = {
                    navController.navigate(Screen.PhotoCapture)
                },
                onViewHistoryClick = {
                    navController.navigate(Screen.TaskHistory)
                },
                onBackClick = {
                    navController.navigate(Screen.NoiseTest) {
                        popUpTo(Screen.NoiseTest) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.TextReading> {
            TextReadingScreen(
                onTaskSubmitted = {
                    navController.popBackStack(Screen.TaskSelection, inclusive = false)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Screen.ImageDescription> {
            ImageDescriptionScreen(
                onTaskSubmitted = {
                    navController.popBackStack(Screen.TaskSelection, inclusive = false)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Screen.PhotoCapture> {
            PhotoCaptureScreen(
                onTaskSubmitted = {
                    navController.popBackStack(Screen.TaskSelection, inclusive = false)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Screen.TaskHistory> {
            TaskHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
