package com.passwordmanager.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val website: String,
    val username: String,
    val encryptedPassword: String,
    val notes: String = "",
    val category: String = "Yleinen",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)