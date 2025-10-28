package com.passwordmanager.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    
    @Query("SELECT * FROM passwords ORDER BY updatedAt DESC")
    fun getAllPasswords(): Flow<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Long): PasswordEntity?
    
    @Query("SELECT * FROM passwords WHERE title LIKE '%' || :query || '%' OR website LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun searchPasswords(query: String): Flow<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords WHERE category = :category ORDER BY updatedAt DESC")
    fun getPasswordsByCategory(category: String): Flow<List<PasswordEntity>>
    
    @Query("SELECT DISTINCT category FROM passwords ORDER BY category")
    fun getAllCategories(): Flow<List<String>>
    
    @Insert
    suspend fun insertPassword(password: PasswordEntity): Long
    
    @Update
    suspend fun updatePassword(password: PasswordEntity)
    
    @Delete
    suspend fun deletePassword(password: PasswordEntity)
    
    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePasswordById(id: Long)
}