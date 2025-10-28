package com.passwordmanager.utils

import org.junit.Test
import org.junit.Assert.*

class PassToolGeneratorTest {
    
    @Test
    fun `generatePassword should produce consistent results`() {
        val passphrase = "test"
        val length = 16
        
        val password1 = PassToolGenerator.generatePassword(passphrase, length)
        val password2 = PassToolGenerator.generatePassword(passphrase, length)
        
        // Sama passphrase tuottaa aina saman salasanan
        assertEquals(password1, password2)
        assertEquals(length, password1.length)
    }
    
    @Test
    fun `generatePassword should handle different lengths`() {
        val passphrase = "test"
        
        val short = PassToolGenerator.generatePassword(passphrase, 8)
        val long = PassToolGenerator.generatePassword(passphrase, 32)
        
        assertEquals(8, short.length)
        assertEquals(32, long.length)
        
        // Lyhyempi salasana on pidemman alku
        assertTrue(long.startsWith(short))
    }
    
    @Test
    fun `generatePassword should handle empty passphrase`() {
        val password = PassToolGenerator.generatePassword("", 16)
        assertEquals(16, password.length)
    }
    
    @Test
    fun `generatePassword should be compatible with bash script logic`() {
        // Testaa että tuottaa oikean tuloksen tunnetulle syötteelle
        val password = PassToolGenerator.generatePassword("test", 43)
        
        // Tämä on SHA256("test") -> base64 -> 43 ensimmäistä merkkiä
        val expected = "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg"
        assertEquals(expected, password)
    }
    
    @Test
    fun `testCompatibility should return true`() {
        assertTrue(PassToolGenerator.testCompatibility())
    }
}