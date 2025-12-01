package com.app.taskrecorder.core.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecordingButton(
    isRecording: Boolean,
    recordingDuration: Long,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(6.dp, CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onStartRecording()
                                tryAwaitRelease()
                                onStopRecording()
                            }
                        )
                    },
                shape = CircleShape,
                color = if (isRecording) Color(0xFFF44336) else Color(0xFF2196F3)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Record",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (isRecording)
                    "Recording... ${recordingDuration / 1000}s"
                else
                    "Press and hold to record",
                fontSize = 14.sp,
                color = if (isRecording) Color(0xFFF44336) else Color(0xFF78909C),
                fontWeight = if (isRecording) FontWeight.Bold else FontWeight.Normal
            )
            if (isRecording) {
                Text(
                    text = "Release to stop",
                    fontSize = 12.sp,
                    color = Color(0xFF78909C)
                )
            }
        }
    }
}
