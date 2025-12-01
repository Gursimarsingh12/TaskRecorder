package com.app.taskrecorder.features.image_description.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.taskrecorder.core.ui.components.AsyncImageCard
import com.app.taskrecorder.core.ui.components.AudioPlayerCard
import com.app.taskrecorder.core.ui.components.RecordingActions
import com.app.taskrecorder.core.ui.components.RecordingButton
import com.app.taskrecorder.core.ui.components.ValidationCheckboxes
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDescriptionScreen(
    onTaskSubmitted: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ImageDescriptionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudioOnExit()
        }
    }
    
    if (!state.hasPermission) {
        com.app.taskrecorder.platform.RequestAudioPermission { granted ->
            if (granted) {
                viewModel.requestPermission()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Image Description Tasks") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Explain what you see in the image in your native language",
                fontSize = 14.sp,
                color = Color(0xFF78909C),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                AsyncImageCard(
                    imageUrl = state.imageUrl,
                    contentDescription = "Image to describe"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (audioState.audioPath == null) {
                RecordingButton(
                    isRecording = audioState.isRecording,
                    recordingDuration = audioState.recordingDuration,
                    onStartRecording = { viewModel.startRecording() },
                    onStopRecording = { viewModel.stopRecording() }
                )
            } else {
                Text(
                    text = "Your Recording",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                AudioPlayerCard(
                    isPlaying = audioState.isPlaying,
                    playbackPosition = audioState.playbackPosition,
                    duration = audioState.recordingDuration,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSeek = { viewModel.seekTo(it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ValidationCheckboxes(
                    noBackgroundNoise = state.noBackgroundNoise,
                    noMistakes = state.noMistakes,
                    noErrorsInMiddle = state.noErrorsInMiddle,
                    onNoBackgroundNoiseChange = viewModel::setNoBackgroundNoise,
                    onNoMistakesChange = viewModel::setNoMistakes,
                    onNoErrorsInMiddleChange = viewModel::setNoErrorsInMiddle
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                RecordingActions(
                    onRecordAgain = { viewModel.recordAgain() },
                    onSubmit = {
                        viewModel.submitTask()
                        onTaskSubmitted()
                    },
                    submitEnabled = state.noBackgroundNoise && state.noMistakes && state.noErrorsInMiddle
                )
            }
            
            audioState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = Color(0xFFF44336),
                    fontSize = 14.sp
                )
            }
        }
    }
}
