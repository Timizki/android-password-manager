package com.passwordmanager.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [PasswordProfileEntity::class],
    version = 2,
    exportSchema = true
)
abstract class PasswordDatabase : RoomDatabase() {
    
    abstract fun passwordProfileDao(): PasswordProfileDao
    
    companion object {
        const val DATABASE_NAME = "password_database"
    }
}