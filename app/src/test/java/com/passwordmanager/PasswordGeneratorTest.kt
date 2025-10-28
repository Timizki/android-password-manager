package com.passwordmanager

import com.passwordmanager.utils.PasswordGenerator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PasswordGeneratorTest {
    
    private lateinit var passwordGenerator: PasswordGenerator
    
    @Before
    fun setUp() {
        passwordGenerator = PasswordGenerator()
    }
    
    @Test
    fun `generatePassword returns correct length`() {
        val length = 12
        val password = passwordGenerator.generatePassword(length = length)
        assertEquals(length, password.length)
    }
    
    @Test
    fun `generatePassword with uppercase includes uppercase letters`() {
        val password = passwordGenerator.generatePassword(
            length = 20,
            includeUppercase = true,
            includeLowercase = false,
            includeNumbers = false,
            includeSymbols = false
        )
        assertTrue(password.any { it.isUpperCase() })
    }
    
    @Test
    fun `generatePassword with lowercase includes lowercase letters`() {
        val password = passwordGenerator.generatePassword(
            length = 20,
            includeUppercase = false,
            includeLowercase = true,
            includeNumbers = false,
            includeSymbols = false
        )
        assertTrue(password.any { it.isLowerCase() })
    }
    
    @Test
    fun `generatePassword with numbers includes digits`() {
        val password = passwordGenerator.generatePassword(
            length = 20,
            includeUppercase = false,
            includeLowercase = false,
            includeNumbers = true,
            includeSymbols = false
        )
        assertTrue(password.any { it.isDigit() })
    }
    
    @Test
    fun `generatePassword with symbols includes special characters`() {
        val password = passwordGenerator.generatePassword(
            length = 20,
            includeUppercase = false,
            includeLowercase = false,
            includeNumbers = false,
            includeSymbols = true
        )
        val symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?"
        assertTrue(password.any { it in symbols })
    }
    
    @Test
    fun `calculatePasswordStrength returns correct strength for weak password`() {
        val strength = passwordGenerator.calculatePasswordStrength("123")
        assertEquals("Heikko", strength)
    }
    
    @Test
    fun `calculatePasswordStrength returns correct strength for medium password`() {
        val strength = passwordGenerator.calculatePasswordStrength("password123")
        assertEquals("Keskitaso", strength)
    }
    
    @Test
    fun `calculatePasswordStrength returns correct strength for strong password`() {
        val strength = passwordGenerator.calculatePasswordStrength("MyStr0ng!P@ssw0rd")
        assertEquals("Vahva", strength)
    }
    
    @Test
    fun `generatePassword with all options disabled throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            passwordGenerator.generatePassword(
                length = 10,
                includeUppercase = false,
                includeLowercase = false,
                includeNumbers = false,
                includeSymbols = false
            )
        }
    }
}