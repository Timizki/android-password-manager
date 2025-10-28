package com.passwordmanager.data.repository

import com.passwordmanager.data.database.PasswordDao
import com.passwordmanager.data.database.PasswordEntity
import com.passwordmanager.domain.model.Password
import com.passwordmanager.domain.repository.PasswordRepository
import com.passwordmanager.utils.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordRepositoryImpl @Inject constructor(
    private val passwordDao: PasswordDao,
    private val cryptoManager: CryptoManager
) : PasswordRepository {
    
    override fun getAllPasswords(): Flow<List<Password>> {
        return passwordDao.getAllPasswords().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getPasswordById(id: Long): Password? {
        return passwordDao.getPasswordById(id)?.toDomainModel()
    }
    
    override fun searchPasswords(query: String): Flow<List<Password>> {
        return passwordDao.searchPasswords(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getPasswordsByCategory(category: String): Flow<List<Password>> {
        return passwordDao.getPasswordsByCategory(category).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getAllCategories(): Flow<List<String>> {
        return passwordDao.getAllCategories()
    }
    
    override suspend fun insertPassword(password: Password): Long {
        val entity = password.toEntity()
        return passwordDao.insertPassword(entity)
    }
    
    override suspend fun updatePassword(password: Password) {
        val entity = password.toEntity()
        passwordDao.updatePassword(entity)
    }
    
    override suspend fun deletePassword(password: Password) {
        val entity = password.toEntity()
        passwordDao.deletePassword(entity)
    }
    
    override suspend fun deletePasswordById(id: Long) {
        passwordDao.deletePasswordById(id)
    }
    
    private fun PasswordEntity.toDomainModel(): Password {
        return Password(
            id = id,
            title = title,
            website = website,
            username = username,
            password = cryptoManager.decrypt(encryptedPassword),
            notes = notes,
            category = category,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun Password.toEntity(): PasswordEntity {
        return PasswordEntity(
            id = id,
            title = title,
            website = website,
            username = username,
            encryptedPassword = cryptoManager.encrypt(password),
            notes = notes,
            category = category,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}