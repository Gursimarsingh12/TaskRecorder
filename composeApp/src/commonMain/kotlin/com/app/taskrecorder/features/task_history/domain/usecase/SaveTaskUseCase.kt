package com.app.taskrecorder.features.task_history.domain.usecase

import com.app.taskrecorder.features.task_history.domain.model.Task
import com.app.taskrecorder.features.task_history.domain.repository.TaskRepository

class SaveTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.saveTask(task)
    }
}
