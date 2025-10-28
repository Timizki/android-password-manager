package com.passwordmanager.domain.usecase.profile

import com.passwordmanager.domain.model.PasswordProfile
import com.passwordmanager.domain.repository.PasswordProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProfilesUseCase @Inject constructor(
    private val repository: PasswordProfileRepository
) {
    operator fun invoke(): Flow<List<PasswordProfile>> {
        return repository.getAllProfiles()
    }
}