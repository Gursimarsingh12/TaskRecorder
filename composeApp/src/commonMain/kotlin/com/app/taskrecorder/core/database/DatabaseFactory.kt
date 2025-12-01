package com.app.taskrecorder.core.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<AppDatabase>
}
