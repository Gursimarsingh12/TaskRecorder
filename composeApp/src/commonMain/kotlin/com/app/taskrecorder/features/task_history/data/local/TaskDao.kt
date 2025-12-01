package com.app.taskrecorder.features.task_history.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: TaskEntity)
    
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?
    
    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCount(): Flow<Int>
    
    @Query("SELECT SUM(durationSec) FROM tasks")
    fun getTotalDuration(): Flow<Long?>
    
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Int)
}
