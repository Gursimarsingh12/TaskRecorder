package com.app.taskrecorder.features.task_history.domain.usecase

import com.app.taskrecorder.features.task_history.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class TaskStats(
    val totalTasks: Int,
    val totalDuration: Long
)

class GetTaskStatsUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<TaskStats> {
        return combine(
            repository.getTaskCount(),
            repository.getTotalDuration()
        ) { count, duration ->
            TaskStats(totalTasks = count, totalDuration = duration)
        }
    }
}
