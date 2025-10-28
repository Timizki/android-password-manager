package com.passwordmanager.domain.usecase.profile

import com.passwordmanager.domain.repository.PasswordProfileRepository
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
    private val repository: PasswordProfileRepository
) {
    suspend operator fun invoke(profileId: Long) {
        repository.deleteProfileById(profileId)
    }
}