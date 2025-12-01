package com.app.taskrecorder.features.task_history.domain.repository

import com.app.taskrecorder.features.task_history.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun saveTask(task: Task)
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Int): Task?
    fun getTaskCount(): Flow<Int>
    fun getTotalDuration(): Flow<Long>
    suspend fun deleteTask(id: Int)
}
