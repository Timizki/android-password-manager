package com.passwordmanager.domain.model

/**
 * Salasanaprofiili - tallentaa vain tiedon salasanan generointisäännöistä,
 * ei itse salasanaa (PassTool-yhteensopiva)
 */
data class PasswordProfile(
    val id: Long = 0,
    val title: String,
    val website: String,
    val username: String,
    val notes: String = "",
    val category: String = "Yleinen",
    val passwordLength: Int = 16,
    val useSpecialChars: Boolean = true,
    val specialChars: String = "!@#$%^&*()_+-=[]{}|;:,.<>?",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)