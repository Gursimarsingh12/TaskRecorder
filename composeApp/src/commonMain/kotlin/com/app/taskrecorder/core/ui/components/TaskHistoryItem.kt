package com.app.taskrecorder.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.taskrecorder.features.task_history.domain.model.Task
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun TaskHistoryItem(
    task: Task,
    onPreviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Task #${task.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F)
                )
                
                val instant = Instant.fromEpochMilliseconds(task.timestamp)
                val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                val dateStr = "${dateTime.day} ${dateTime.month.name.take(3)}, ${dateTime.year}"
                val timeStr = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
                
                Text(
                    text = "Duration ${task.durationSec}sec | $dateStr | $timeStr",
                    fontSize = 12.sp,
                    color = Color(0xFF78909C)
                )
            }
            
            IconButton(onClick = onPreviewClick) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Preview",
                    tint = Color(0xFF2196F3)
                )
            }
        }
    }
}
