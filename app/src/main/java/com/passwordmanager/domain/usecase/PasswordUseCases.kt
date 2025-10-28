package com.passwordmanager.domain.usecase

import com.passwordmanager.domain.model.Password
import com.passwordmanager.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPasswordsUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    operator fun invoke(): Flow<List<Password>> {
        return repository.getAllPasswords()
    }
}

class GetPasswordByIdUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(id: Long): Password? {
        return repository.getPasswordById(id)
    }
}

class SearchPasswordsUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    operator fun invoke(query: String): Flow<List<Password>> {
        return repository.searchPasswords(query)
    }
}

class GetPasswordsByCategoryUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    operator fun invoke(category: String): Flow<List<Password>> {
        return repository.getPasswordsByCategory(category)
    }
}

class GetAllCategoriesUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return repository.getAllCategories()
    }
}

class InsertPasswordUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(password: Password): Long {
        if (password.title.isBlank()) {
            throw IllegalArgumentException("Otsikko ei voi olla tyhjä")
        }
        if (password.password.isBlank()) {
            throw IllegalArgumentException("Salasana ei voi olla tyhjä")
        }
        return repository.insertPassword(password)
    }
}

class UpdatePasswordUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(password: Password) {
        if (password.title.isBlank()) {
            throw IllegalArgumentException("Otsikko ei voi olla tyhjä")
        }
        if (password.password.isBlank()) {
            throw IllegalArgumentException("Salasana ei voi olla tyhjä")
        }
        repository.updatePassword(password.copy(updatedAt = System.currentTimeMillis()))
    }
}

class DeletePasswordUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(password: Password) {
        repository.deletePassword(password)
    }
}

class DeletePasswordByIdUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deletePasswordById(id)
    }
}

data class PasswordUseCases(
    val getAllPasswords: GetAllPasswordsUseCase,
    val getPasswordById: GetPasswordByIdUseCase,
    val searchPasswords: SearchPasswordsUseCase,
    val getPasswordsByCategory: GetPasswordsByCategoryUseCase,
    val getAllCategories: GetAllCategoriesUseCase,
    val insertPassword: InsertPasswordUseCase,
    val updatePassword: UpdatePasswordUseCase,
    val deletePassword: DeletePasswordUseCase,
    val deletePasswordById: DeletePasswordByIdUseCase
)