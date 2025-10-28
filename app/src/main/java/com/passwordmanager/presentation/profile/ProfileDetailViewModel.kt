package com.passwordmanager.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passwordmanager.domain.model.PasswordProfile
import com.passwordmanager.domain.repository.PasswordProfileRepository
import com.passwordmanager.domain.usecase.profile.DeleteProfileUseCase
import com.passwordmanager.domain.usecase.profile.GeneratePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val repository: PasswordProfileRepository,
    private val generatePasswordUseCase: GeneratePasswordUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState: StateFlow<ProfileDetailUiState> = _uiState.asStateFlow()
    
    fun loadProfile(profileId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val profile = repository.getProfileById(profileId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile,
                    error = if (profile == null) "Profiilia ei löytynyt" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Tuntematon virhe"
                )
            }
        }
    }
    
    fun generatePassword(passphrase: String) {
        val profile = _uiState.value.profile ?: return
        
        viewModelScope.launch {
            try {
                val password = generatePasswordUseCase(profile, passphrase)
                _uiState.value = _uiState.value.copy(
                    generatedPassword = password,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Salasanan generointi epäonnistui"
                )
            }
        }
    }
    
    fun deleteProfile() {
        val profile = _uiState.value.profile ?: return
        
        viewModelScope.launch {
            try {
                deleteProfileUseCase(profile.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Profiilin poisto epäonnistui"
                )
            }
        }
    }
}

data class ProfileDetailUiState(
    val isLoading: Boolean = false,
    val profile: PasswordProfile? = null,
    val generatedPassword: String? = null,
    val error: String? = null
)