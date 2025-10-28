package com.passwordmanager.domain.repository

import com.passwordmanager.domain.model.PasswordProfile
import kotlinx.coroutines.flow.Flow

interface PasswordProfileRepository {
    
    fun getAllProfiles(): Flow<List<PasswordProfile>>
    
    suspend fun getProfileById(id: Long): PasswordProfile?
    
    fun searchProfiles(query: String): Flow<List<PasswordProfile>>
    
    fun getProfilesByCategory(category: String): Flow<List<PasswordProfile>>
    
    fun getAllCategories(): Flow<List<String>>
    
    suspend fun insertProfile(profile: PasswordProfile): Long
    
    suspend fun updateProfile(profile: PasswordProfile)
    
    suspend fun deleteProfile(profile: PasswordProfile)
    
    suspend fun deleteProfileById(id: Long)
}