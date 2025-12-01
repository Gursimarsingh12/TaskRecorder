package com.app.taskrecorder.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.taskrecorder.features.task_history.data.local.TaskEntity
import com.app.taskrecorder.features.task_history.data.local.TaskDao

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
