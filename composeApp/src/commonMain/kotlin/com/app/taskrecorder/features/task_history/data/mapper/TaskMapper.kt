package com.app.taskrecorder.features.task_history.data.mapper

import com.app.taskrecorder.features.task_history.data.local.TaskEntity
import com.app.taskrecorder.features.task_history.domain.model.Task
import com.app.taskrecorder.features.task_history.domain.model.TaskType

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        taskType = TaskType.valueOf(taskType),
        text = text,
        imageUrl = imageUrl,
        imagePath = imagePath,
        audioPath = audioPath,
        durationSec = durationSec,
        timestamp = timestamp
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        taskType = taskType.name,
        text = text,
        imageUrl = imageUrl,
        imagePath = imagePath,
        audioPath = audioPath,
        durationSec = durationSec,
        timestamp = timestamp
    )
}

fun List<TaskEntity>.toDomain(): List<Task> {
    return map { it.toDomain() }
}
