package com.app.taskrecorder.features.task_history.domain.model

data class Task(
    val id: Int,
    val taskType: TaskType,
    val text: String?,
    val imageUrl: String?,
    val imagePath: String?,
    val audioPath: String,
    val durationSec: Long,
    val timestamp: Long
)

enum class TaskType {
    TEXT_READING,
    IMAGE_DESCRIPTION,
    PHOTO_CAPTURE
}
