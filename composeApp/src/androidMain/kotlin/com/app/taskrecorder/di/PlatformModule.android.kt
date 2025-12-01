package com.app.taskrecorder.di

import com.app.taskrecorder.core.audio.AudioPlayer
import com.app.taskrecorder.core.audio.AudioRecorder
import com.app.taskrecorder.core.audio.NoiseDetector
import com.app.taskrecorder.core.camera.CameraController
import com.app.taskrecorder.core.database.DatabaseFactory
import com.app.taskrecorder.platform.audio.AndroidAudioPlayer
import com.app.taskrecorder.platform.audio.AndroidAudioRecorder
import com.app.taskrecorder.platform.audio.AndroidNoiseDetector
import com.app.taskrecorder.platform.camera.AndroidCameraController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::DatabaseFactory)
    singleOf(::AndroidAudioRecorder) bind AudioRecorder::class
    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class
    singleOf(::AndroidNoiseDetector) bind NoiseDetector::class
    singleOf(::AndroidCameraController) bind CameraController::class
}
