package com.app.taskrecorder.features.task_history.data.repository

import com.app.taskrecorder.features.task_history.data.local.TaskDao
import com.app.taskrecorder.features.task_history.data.mapper.toDomain
import com.app.taskrecorder.features.task_history.data.mapper.toEntity
import com.app.taskrecorder.features.task_history.domain.model.Task
import com.app.taskrecorder.features.task_history.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {
    
    override suspend fun saveTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }
    
    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { it.toDomain() }
    }
    
    override suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }
    
    override fun getTaskCount(): Flow<Int> {
        return taskDao.getTaskCount()
    }
    
    override fun getTotalDuration(): Flow<Long> {
        return taskDao.getTotalDuration().map { it ?: 0L }
    }
    
    override suspend fun deleteTask(id: Int) {
        taskDao.deleteTask(id)
    }
}
