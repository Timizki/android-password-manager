package com.passwordmanager.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [PasswordEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PasswordDatabase : RoomDatabase() {
    
    abstract fun passwordDao(): PasswordDao
    
    companion object {
        const val DATABASE_NAME = "password_database"
    }
}