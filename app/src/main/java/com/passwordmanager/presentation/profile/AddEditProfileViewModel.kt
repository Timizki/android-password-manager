package com.passwordmanager.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passwordmanager.domain.model.PasswordProfile
import com.passwordmanager.domain.usecase.profile.AddProfileUseCase
import com.passwordmanager.domain.usecase.profile.UpdateProfileUseCase
import com.passwordmanager.domain.repository.PasswordProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditProfileViewModel @Inject constructor(
    private val repository: PasswordProfileRepository,
    private val addProfileUseCase: AddProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddEditProfileUiState())
    val uiState: StateFlow<AddEditProfileUiState> = _uiState.asStateFlow()
    
    private var profileId: Long? = null
    
    fun loadProfile(id: Long) {
        profileId = id
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val profile = repository.getProfileById(id)
                if (profile != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        title = profile.title,
                        website = profile.website,
                        username = profile.username,
                        category = profile.category,
                        passwordLength = profile.passwordLength,
                        useSpecialChars = profile.useSpecialChars,
                        specialChars = profile.specialChars,
                        notes = profile.notes,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Profiilia ei löytynyt"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Tuntematon virhe"
                )
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = if (title.isBlank()) "Otsikko on pakollinen" else null
        )
    }
    
    fun updateWebsite(website: String) {
        _uiState.value = _uiState.value.copy(website = website)
    }
    
    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }
    
    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }
    
    fun updatePasswordLength(length: Int) {
        val error = when {
            length <= 0 -> "Pituus täytyy olla positiivinen"
            length > 128 -> "Pituus ei voi olla yli 128 merkkiä"
            else -> null
        }
        
        _uiState.value = _uiState.value.copy(
            passwordLength = length,
            passwordLengthError = error
        )
    }
    
    fun updateUseSpecialChars(use: Boolean) {
        _uiState.value = _uiState.value.copy(useSpecialChars = use)
    }
    
    fun updateSpecialChars(chars: String) {
        _uiState.value = _uiState.value.copy(specialChars = chars)
    }
    
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }
    
    fun saveProfile() {
        val state = _uiState.value
        
        // Validointi
        if (state.title.isBlank()) {
            _uiState.value = state.copy(titleError = "Otsikko on pakollinen")
            return
        }
        
        if (state.passwordLength <= 0 || state.passwordLength > 128) {
            _uiState.value = state.copy(
                passwordLengthError = "Pituus täytyy olla 1-128 merkkiä"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val profile = PasswordProfile(
                    id = profileId ?: 0,
                    title = state.title,
                    website = state.website,
                    username = state.username,
                    category = state.category,
                    passwordLength = state.passwordLength,
                    useSpecialChars = state.useSpecialChars,
                    specialChars = state.specialChars,
                    notes = state.notes
                )
                
                if (profileId != null) {
                    updateProfileUseCase(profile)
                } else {
                    addProfileUseCase(profile)
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Tallentaminen epäonnistui"
                )
            }
        }
    }
}

data class AddEditProfileUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val title: String = "",
    val website: String = "",
    val username: String = "",
    val category: String = "Yleinen",
    val passwordLength: Int = 16,
    val useSpecialChars: Boolean = true,
    val specialChars: String = "!@#$%^&*()_+-=[]{}|;:,.<>?",
    val notes: String = "",
    val titleError: String? = null,
    val passwordLengthError: String? = null,
    val error: String? = null
)