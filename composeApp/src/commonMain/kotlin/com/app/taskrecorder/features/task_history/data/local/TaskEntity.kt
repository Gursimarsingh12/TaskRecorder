package com.app.taskrecorder.features.task_history.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskType: String,
    val text: String?,
    val imageUrl: String?,
    val imagePath: String?,
    val audioPath: String,
    val durationSec: Long,
    val timestamp: Long
)
