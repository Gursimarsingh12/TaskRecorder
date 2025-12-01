package com.app.taskrecorder.platform

import androidx.compose.runtime.Composable

@Composable
expect fun RequestAudioPermission(
    onPermissionResult: (Boolean) -> Unit
)

@Composable
expect fun RequestCameraPermission(
    onPermissionResult: (Boolean) -> Unit
)
