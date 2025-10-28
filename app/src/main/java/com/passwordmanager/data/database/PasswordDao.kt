package com.passwordmanager.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordProfileDao {
    
    @Query("SELECT * FROM password_profiles ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<PasswordProfileEntity>>
    
    @Query("SELECT * FROM password_profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): PasswordProfileEntity?
    
    @Query("SELECT * FROM password_profiles WHERE title LIKE '%' || :query || '%' OR website LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun searchProfiles(query: String): Flow<List<PasswordProfileEntity>>
    
    @Query("SELECT * FROM password_profiles WHERE category = :category ORDER BY updatedAt DESC")
    fun getProfilesByCategory(category: String): Flow<List<PasswordProfileEntity>>
    
    @Query("SELECT DISTINCT category FROM password_profiles ORDER BY category")
    fun getAllCategories(): Flow<List<String>>
    
    @Insert
    suspend fun insertProfile(profile: PasswordProfileEntity): Long
    
    @Update
    suspend fun updateProfile(profile: PasswordProfileEntity)
    
    @Delete
    suspend fun deleteProfile(profile: PasswordProfileEntity)
    
    @Query("DELETE FROM password_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)
}