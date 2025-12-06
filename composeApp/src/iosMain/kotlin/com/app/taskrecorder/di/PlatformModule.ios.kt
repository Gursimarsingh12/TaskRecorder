package com.app.taskrecorder.di

import com.app.taskrecorder.core.audio.AudioPlayer
import com.app.taskrecorder.core.audio.AudioRecorder
import com.app.taskrecorder.core.audio.NoiseDetector
import com.app.taskrecorder.core.camera.CameraController
import com.app.taskrecorder.core.database.DatabaseFactory
import com.app.taskrecorder.platform.audio.IOSAudioPlayer
import com.app.taskrecorder.platform.audio.IOSAudioRecorder
import com.app.taskrecorder.platform.audio.IOSNoiseDetector
import com.app.taskrecorder.platform.camera.IOSCameraController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::DatabaseFactory)
    singleOf(::IOSAudioRecorder) bind AudioRecorder::class
    singleOf(::IOSAudioPlayer) bind AudioPlayer::class
    singleOf(::IOSNoiseDetector) bind NoiseDetector::class
    singleOf(::IOSCameraController) bind CameraController::class
}
