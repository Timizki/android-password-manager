package com.passwordmanager.di

import com.passwordmanager.data.repository.PasswordRepositoryImpl
import com.passwordmanager.domain.repository.PasswordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindPasswordRepository(
        passwordRepositoryImpl: PasswordRepositoryImpl
    ): PasswordRepository
}