package com.passwordmanager.presentation.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passwordmanager.domain.model.Password
import com.passwordmanager.domain.usecase.PasswordUseCases
import com.passwordmanager.utils.PasswordGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases,
    private val passwordGenerator: PasswordGenerator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val passwordId: Long = savedStateHandle.get<Long>("passwordId") ?: -1L
    
    private val _uiState = MutableStateFlow(AddEditPasswordUiState())
    val uiState: StateFlow<AddEditPasswordUiState> = _uiState.asStateFlow()
    
    init {
        if (passwordId != -1L) {
            loadPassword(passwordId)
        }
    }
    
    private fun loadPassword(id: Long) {
        viewModelScope.launch {
            passwordUseCases.getPasswordById(id)?.let { password ->
                _uiState.value = _uiState.value.copy(
                    title = password.title,
                    website = password.website,
                    username = password.username,
                    password = password.password,
                    notes = password.notes,
                    category = password.category,
                    isEditMode = true
                )
            }
        }
    }
    
    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }
    
    fun onWebsiteChanged(website: String) {
        _uiState.value = _uiState.value.copy(website = website)
    }
    
    fun onUsernameChanged(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }
    
    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    
    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }
    
    fun onCategoryChanged(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }
    
    fun generatePassword() {
        val currentState = _uiState.value
        val generatedPassword = passwordGenerator.generatePassword(
            length = currentState.passwordLength,
            includeUppercase = currentState.includeUppercase,
            includeLowercase = currentState.includeLowercase,
            includeNumbers = currentState.includeNumbers,
            includeSymbols = currentState.includeSymbols
        )
        _uiState.value = currentState.copy(password = generatedPassword)
    }
    
    fun onPasswordLengthChanged(length: Int) {
        _uiState.value = _uiState.value.copy(passwordLength = length)
    }
    
    fun onIncludeUppercaseChanged(include: Boolean) {
        _uiState.value = _uiState.value.copy(includeUppercase = include)
    }
    
    fun onIncludeLowercaseChanged(include: Boolean) {
        _uiState.value = _uiState.value.copy(includeLowercase = include)
    }
    
    fun onIncludeNumbersChanged(include: Boolean) {
        _uiState.value = _uiState.value.copy(includeNumbers = include)
    }
    
    fun onIncludeSymbolsChanged(include: Boolean) {
        _uiState.value = _uiState.value.copy(includeSymbols = include)
    }
    
    fun savePassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            if (currentState.title.isBlank()) {
                _uiState.value = currentState.copy(errorMessage = "Otsikko on pakollinen")
                return@launch
            }
            
            if (currentState.password.isBlank()) {
                _uiState.value = currentState.copy(errorMessage = "Salasana on pakollinen")
                return@launch
            }
            
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
            
            try {
                val password = Password(
                    id = if (currentState.isEditMode) passwordId else 0,
                    title = currentState.title,
                    website = currentState.website,
                    username = currentState.username,
                    password = currentState.password,
                    notes = currentState.notes,
                    category = currentState.category
                )
                
                if (currentState.isEditMode) {
                    passwordUseCases.updatePassword(password)
                } else {
                    passwordUseCases.insertPassword(password)
                }
                
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Virhe tallentamisessa"
                )
            }
        }
    }
}

data class AddEditPasswordUiState(
    val title: String = "",
    val website: String = "",
    val username: String = "",
    val password: String = "",
    val notes: String = "",
    val category: String = "Yleinen",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordLength: Int = 16,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = true
)