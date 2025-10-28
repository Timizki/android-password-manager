package com.passwordmanager.domain.usecase.profile

import com.passwordmanager.domain.model.PasswordProfile
import com.passwordmanager.domain.repository.PasswordProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: PasswordProfileRepository
) {
    suspend operator fun invoke(profile: PasswordProfile) {
        require(profile.title.isNotBlank()) { "Otsikko ei voi olla tyhjä" }
        require(profile.passwordLength > 0) { "Salasanan pituus täytyy olla positiivinen" }
        require(profile.passwordLength <= 128) { "Salasanan pituus ei voi olla yli 128 merkkiä" }
        
        repository.updateProfile(profile)
    }
}