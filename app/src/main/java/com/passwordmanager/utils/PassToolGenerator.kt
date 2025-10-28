package com.passwordmanager.utils

import java.security.MessageDigest
import java.util.Base64

/**
 * PassTool-yhteensopiva salasanageneraattori
 * Käyttää samaa logiikkaa kuin bash-skripti:
 * passphrase -> SHA256 -> base64 -> substring
 */
object PassToolGenerator {
    
    /**
     * Generoi salasanan PassTool-logiikalla
     * @param passphrase Käyttäjän antama salainen passphrase
     * @param length Haluttu salasanan pituus
     * @param useSpecialChars Käytetäänkö erikoismerkkejä (ei vaikuta PassTool-logiikkaan)
     * @param specialChars Erikoismerkit (ei vaikuta PassTool-logiikkaan)
     * @return Generoitu salasana
     */
    fun generatePassword(
        passphrase: String,
        length: Int,
        useSpecialChars: Boolean = true,
        specialChars: String = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    ): String {
        // 1. Laske SHA256-hash passphrasesta
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(passphrase.toByteArray())
        
        // 2. Muunna base64:ksi
        val base64Hash = Base64.getEncoder().encodeToString(hashBytes)
        
        // 3. Ota haluttu pituus
        val password = if (length <= base64Hash.length) {
            base64Hash.substring(0, length)
        } else {
            // Jos haluttu pituus on pidempi kuin hash, toista hash
            val repeated = base64Hash.repeat((length / base64Hash.length) + 1)
            repeated.substring(0, length)
        }
        
        return password
    }
    
    /**
     * Testaa että generointi toimii samoin kuin bash-skripti
     */
    fun testCompatibility(): Boolean {
        // Testi: "test" -> pitäisi tuottaa "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg="
        val testResult = generatePassword("test", 44)
        val expectedStart = "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg="
        return testResult == expectedStart
    }
}