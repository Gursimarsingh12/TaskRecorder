package com.app.taskrecorder.platform.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraPreviewComposable(
    onImageCaptured: (String) -> Unit,
    onError: (String) -> Unit,
    captureImage: Boolean,
    onCaptureComplete: () -> Unit,
    modifier: Modifier = Modifier
)
