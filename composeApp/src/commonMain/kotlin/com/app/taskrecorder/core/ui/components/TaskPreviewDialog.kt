package com.app.taskrecorder.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.taskrecorder.features.task_history.domain.model.Task
import com.app.taskrecorder.features.task_history.domain.model.TaskType

@Composable
fun TaskPreviewDialog(
    task: Task,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task Preview",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF37474F)
                        )
                    }
                }
                
                HorizontalDivider()
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Task Type: ${task.taskType.name.replace('_', ' ')}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF37474F)
                    )
                    
                    when (task.taskType) {
                        TaskType.TEXT_READING -> {
                            task.text?.let {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(Color(0xFFF5F5F5))
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 14.sp,
                                        color = Color(0xFF37474F)
                                    )
                                }
                            }
                        }
                        TaskType.IMAGE_DESCRIPTION -> {
                            task.imageUrl?.let {
                                AsyncImageCard(
                                    imageUrl = it,
                                    contentDescription = "Task Image"
                                )
                            }
                        }
                        TaskType.PHOTO_CAPTURE -> {
                            task.imagePath?.let {
                                AsyncImageCard(
                                    imageUrl = "file://$it",
                                    contentDescription = "Captured Photo"
                                )
                            }
                        }
                    }

                    task.audioPath.let {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(Color(0xFFE3F2FD))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Audio Recording",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF37474F)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Duration: ${task.durationSec}s",
                                    fontSize = 13.sp,
                                    color = Color(0xFF78909C)
                                )
                                Text(
                                    text = "File: ${it.substringAfterLast('/')}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF78909C)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
