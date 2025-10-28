package com.passwordmanager.di

import android.content.Context
import androidx.room.Room
import com.passwordmanager.data.database.PasswordProfileDao
import com.passwordmanager.data.database.PasswordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun providePasswordDatabase(@ApplicationContext context: Context): PasswordDatabase {
        return Room.databaseBuilder(
            context,
            PasswordDatabase::class.java,
            PasswordDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration() // Sallii tietokannan uudelleenluonnin
        .build()
    }
    
    @Provides
    fun providePasswordProfileDao(database: PasswordDatabase): PasswordProfileDao {
        return database.passwordProfileDao()
    }
}