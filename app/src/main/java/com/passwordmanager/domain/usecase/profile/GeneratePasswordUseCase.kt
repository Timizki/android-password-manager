package com.passwordmanager.domain.usecase.profile

import com.passwordmanager.domain.model.PasswordProfile
import com.passwordmanager.utils.PassToolGenerator
import javax.inject.Inject

class GeneratePasswordUseCase @Inject constructor() {
    
    /**
     * Generoi salasanan profiilille käyttäjän passphrasella
     */
    operator fun invoke(profile: PasswordProfile, passphrase: String): String {
        require(passphrase.isNotBlank()) { "Passphrase ei voi olla tyhjä" }
        
        return PassToolGenerator.generatePassword(
            passphrase = passphrase,
            length = profile.passwordLength,
            useSpecialChars = profile.useSpecialChars,
            specialChars = profile.specialChars
        )
    }
}