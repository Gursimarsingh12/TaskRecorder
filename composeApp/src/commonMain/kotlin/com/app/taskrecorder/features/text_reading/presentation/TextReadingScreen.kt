package com.app.taskrecorder.features.text_reading.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.taskrecorder.core.ui.components.AudioPlayerCard
import com.app.taskrecorder.core.ui.components.RecordingActions
import com.app.taskrecorder.core.ui.components.RecordingButton
import com.app.taskrecorder.core.ui.components.ValidationCheckboxes
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextReadingScreen(
    onTaskSubmitted: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: TextReadingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudioOnExit()
        }
    }

    if (!state.hasPermission) {
        com.app.taskrecorder.platform.RequestAudioPermission {
            if (it) viewModel.requestPermission()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Test Reading Task") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Read the passage aloud in your native language.",
                fontSize = 14.sp,
                color = Color(0xFF78909C)
            )
            Spacer(modifier = Modifier.height(12.dp))

            state.product?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color(0xFFF5F5F5))
                ) {
                    Text(
                        text = it.description,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (audioState.audioPath == null) {
                RecordingButton(
                    isRecording = audioState.isRecording,
                    recordingDuration = audioState.recordingDuration,
                    onStartRecording = { viewModel.startRecording() },
                    onStopRecording = { viewModel.stopRecording() },
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            if (audioState.audioPath != null) {
                Spacer(Modifier.height(16.dp))
                Text(text = "Your Recording", fontSize = 16.sp, fontWeight = FontWeight.Medium)

                AudioPlayerCard(
                    isPlaying = audioState.isPlaying,
                    playbackPosition = audioState.playbackPosition,
                    duration = audioState.recordingDuration,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSeek = { viewModel.seekTo(it) },
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(Modifier.height(16.dp))

                ValidationCheckboxes(
                    noBackgroundNoise = state.noBackgroundNoise,
                    noMistakes = state.noMistakes,
                    noErrorsInMiddle = state.noErrorsInMiddle,
                    onNoBackgroundNoiseChange = viewModel::setNoBackgroundNoise,
                    onNoMistakesChange = viewModel::setNoMistakes,
                    onNoErrorsInMiddleChange = viewModel::setNoErrorsInMiddle
                )

                Spacer(Modifier.height(16.dp))

                RecordingActions(
                    onRecordAgain = { viewModel.recordAgain() },
                    onSubmit = {
                        viewModel.submitTask()
                        onTaskSubmitted()
                    },
                    submitEnabled = state.noBackgroundNoise && state.noMistakes && state.noErrorsInMiddle
                )
            }

            audioState.errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, fontSize = 14.sp, color = Color.Red)
            }
        }
    }
}
