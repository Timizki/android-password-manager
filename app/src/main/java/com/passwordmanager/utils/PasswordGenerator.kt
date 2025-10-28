package com.passwordmanager.utils

import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordGenerator @Inject constructor() {
    
    private val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
    private val numberChars = "0123456789"
    private val symbolChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    
    fun generatePassword(
        length: Int = 16,
        includeUppercase: Boolean = true,
        includeLowercase: Boolean = true,
        includeNumbers: Boolean = true,
        includeSymbols: Boolean = true
    ): String {
        if (length < 4) throw IllegalArgumentException("Salasanan pituuden tulee olla vähintään 4 merkkiä")
        
        val charPool = buildString {
            if (includeUppercase) append(uppercaseChars)
            if (includeLowercase) append(lowercaseChars)
            if (includeNumbers) append(numberChars)
            if (includeSymbols) append(symbolChars)
        }
        
        if (charPool.isEmpty()) {
            throw IllegalArgumentException("Vähintään yksi merkkiluokka tulee valita")
        }
        
        val password = StringBuilder()
        
        // Varmista että vähintään yksi merkki jokaisesta valitusta luokasta
        if (includeUppercase) password.append(uppercaseChars.random())
        if (includeLowercase) password.append(lowercaseChars.random())
        if (includeNumbers) password.append(numberChars.random())
        if (includeSymbols) password.append(symbolChars.random())
        
        // Täytä loput satunnaisilla merkeillä
        repeat(length - password.length) {
            password.append(charPool.random())
        }
        
        // Sekoita merkit
        return password.toString().toCharArray().apply {
            shuffle(Random.Default)
        }.concatToString()
    }
    
    fun calculatePasswordStrength(password: String): PasswordStrength {
        var score = 0
        
        // Pituus
        when {
            password.length >= 12 -> score += 2
            password.length >= 8 -> score += 1
        }
        
        // Merkkiluokat
        if (password.any { it.isUpperCase() }) score += 1
        if (password.any { it.isLowerCase() }) score += 1
        if (password.any { it.isDigit() }) score += 1
        if (password.any { it in symbolChars }) score += 1
        
        // Toistuvat merkit
        if (password.groupBy { it }.none { it.value.size > 2 }) score += 1
        
        return when (score) {
            in 0..2 -> PasswordStrength.WEAK
            in 3..4 -> PasswordStrength.MEDIUM
            in 5..6 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }
}

enum class PasswordStrength(val displayName: String, val color: Long) {
    WEAK("Heikko", 0xFFE53E3E),
    MEDIUM("Keskinkertainen", 0xFFDD6B20),
    STRONG("Vahva", 0xFF38A169),
    VERY_STRONG("Erittäin vahva", 0xFF2D3748)
}