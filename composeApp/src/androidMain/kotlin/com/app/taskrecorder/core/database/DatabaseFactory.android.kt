package com.app.taskrecorder.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("task_recorder.db")
        return Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
