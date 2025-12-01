package com.app.taskrecorder.features.task_history.domain.usecase

import com.app.taskrecorder.features.task_history.domain.model.Task
import com.app.taskrecorder.features.task_history.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetAllTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getAllTasks()
    }
}
