package com.passwordmanager.domain.repository

import com.passwordmanager.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    
    fun getAllPasswords(): Flow<List<Password>>
    
    suspend fun getPasswordById(id: Long): Password?
    
    fun searchPasswords(query: String): Flow<List<Password>>
    
    fun getPasswordsByCategory(category: String): Flow<List<Password>>
    
    fun getAllCategories(): Flow<List<String>>
    
    suspend fun insertPassword(password: Password): Long
    
    suspend fun updatePassword(password: Password)
    
    suspend fun deletePassword(password: Password)
    
    suspend fun deletePasswordById(id: Long)
}