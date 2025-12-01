package com.app.taskrecorder

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}
