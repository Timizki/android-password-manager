package com.passwordmanager.data.repository

import com.passwordmanager.data.database.PasswordProfileDao
import com.passwordmanager.data.database.PasswordProfileEntity
import com.passwordmanager.domain.model.PasswordProfile
import com.passwordmanager.domain.repository.PasswordProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordProfileRepositoryImpl @Inject constructor(
    private val passwordProfileDao: PasswordProfileDao
) : PasswordProfileRepository {

    override fun getAllProfiles(): Flow<List<PasswordProfile>> {
        return passwordProfileDao.getAllProfiles().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getProfileById(id: Long): PasswordProfile? {
        return passwordProfileDao.getProfileById(id)?.toDomainModel()
    }

    override fun searchProfiles(query: String): Flow<List<PasswordProfile>> {
        return passwordProfileDao.searchProfiles(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getProfilesByCategory(category: String): Flow<List<PasswordProfile>> {
        return passwordProfileDao.getProfilesByCategory(category).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllCategories(): Flow<List<String>> {
        return passwordProfileDao.getAllCategories()
    }

    override suspend fun insertProfile(profile: PasswordProfile): Long {
        return passwordProfileDao.insertProfile(profile.toEntity())
    }

    override suspend fun updateProfile(profile: PasswordProfile) {
        passwordProfileDao.updateProfile(profile.toEntity().copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteProfile(profile: PasswordProfile) {
        passwordProfileDao.deleteProfile(profile.toEntity())
    }

    override suspend fun deleteProfileById(id: Long) {
        passwordProfileDao.deleteProfileById(id)
    }
}

// Extension functions for mapping
private fun PasswordProfileEntity.toDomainModel(): PasswordProfile {
    return PasswordProfile(
        id = id,
        title = title,
        website = website,
        username = username,
        notes = notes,
        category = category,
        passwordLength = passwordLength,
        useSpecialChars = useSpecialChars,
        specialChars = specialChars,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun PasswordProfile.toEntity(): PasswordProfileEntity {
    return PasswordProfileEntity(
        id = id,
        title = title,
        website = website,
        username = username,
        notes = notes,
        category = category,
        passwordLength = passwordLength,
        useSpecialChars = useSpecialChars,
        specialChars = specialChars,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}