package com.app.taskrecorder.features.photo_capture.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.taskrecorder.core.ui.components.AudioPlayerCard
import com.app.taskrecorder.core.ui.components.RecordingActions
import com.app.taskrecorder.core.ui.components.RecordingButton
import com.app.taskrecorder.core.ui.components.ValidationCheckboxes
import com.app.taskrecorder.platform.camera.CameraPreviewComposable
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCaptureScreen(
    onTaskSubmitted: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: PhotoCaptureViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    var wantsToRecord by remember { mutableStateOf(false) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudioOnExit()
        }
    }
    
    if (!state.hasCameraPermission) {
        com.app.taskrecorder.platform.RequestCameraPermission { granted ->
            if (granted) {
                viewModel.requestCameraPermission()
            }
        }
    }
    
    if (!state.hasAudioPermission) {
        com.app.taskrecorder.platform.RequestAudioPermission { granted ->
            if (granted) {
                viewModel.requestAudioPermission()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Photo Capture Task") },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Take a photo and describe what you see in your native language",
                fontSize = 14.sp,
                color = Color(0xFF78909C),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            var shouldCapture by remember { mutableStateOf(false) }
            
            if (state.imagePath == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                ) {
                    CameraPreviewComposable(
                        onImageCaptured = { path -> viewModel.onPhotoCaptured(path) },
                        onError = { error -> viewModel.onCameraError(error) },
                        captureImage = shouldCapture,
                        onCaptureComplete = { shouldCapture = false },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    FloatingActionButton(
                        onClick = { shouldCapture = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        containerColor = Color(0xFF2196F3)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            tint = Color.White
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPhotoDialog = true },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    CoilImage(
                        imageModel = { "file://${state.imagePath}" },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            contentDescription = "Captured photo"
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { 
                        viewModel.retakePhoto()
                        wantsToRecord = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    )
                ) {
                    Text(text = "Retake Photo")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (state.imagePath != null) {
                if (!wantsToRecord && audioState.audioPath == null && !audioState.isRecording && audioState.recordingDuration == 0L) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.submitTask()
                                onTaskSubmitted()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text(text = "Submit Photo")
                        }
                        
                        Button(
                            onClick = { wantsToRecord = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text(text = "Add Recording")
                        }
                    }
                } else if (audioState.audioPath == null) {
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
    
    if (showPhotoDialog && state.imagePath != null) {
        Dialog(
            onDismissRequest = { showPhotoDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                CoilImage(
                    imageModel = { "file://${state.imagePath}" },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                        contentDescription = "Full photo"
                    ),
                    modifier = Modifier.fillMaxSize()
                )
                
                IconButton(
                    onClick = { showPhotoDialog = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
