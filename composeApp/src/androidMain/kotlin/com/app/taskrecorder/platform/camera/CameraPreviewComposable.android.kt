package com.app.taskrecorder.platform.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun CameraPreviewComposable(
    onImageCaptured: (String) -> Unit,
    onError: (String) -> Unit,
    captureImage: Boolean,
    onCaptureComplete: () -> Unit,
    modifier: Modifier
) {
    CameraPreview(
        onImageCaptured = { uri -> onImageCaptured(uri.toString()) },
        onError = { exception -> onError(exception.message ?: "Unknown error") },
        captureImage = captureImage,
        onCaptureComplete = onCaptureComplete
    )
}
