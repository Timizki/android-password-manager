package com.passwordmanager.domain.model

data class Password(
    val id: Long = 0,
    val title: String,
    val website: String,
    val username: String,
    val password: String,
    val notes: String = "",
    val category: String = "Yleinen",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)