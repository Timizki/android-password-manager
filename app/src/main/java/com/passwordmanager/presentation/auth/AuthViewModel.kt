package com.passwordmanager.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passwordmanager.utils.BiometricAuthManager
import com.passwordmanager.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkAuthenticationStatus()
    }
    
    private fun checkAuthenticationStatus() {
        val isMasterPasswordSet = preferencesManager.isMasterPasswordSet()
        _uiState.value = _uiState.value.copy(
            isFirstTime = !isMasterPasswordSet,
            isBiometricEnabled = preferencesManager.isBiometricEnabled()
        )
    }
    
    fun onMasterPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            masterPassword = password,
            errorMessage = null
        )
    }
    
    fun onConfirmPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = password,
            errorMessage = null
        )
    }
    
    fun onBiometricToggled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isBiometricEnabled = enabled)
    }
    
    fun setupMasterPassword() {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            if (currentState.masterPassword.length < 6) {
                _uiState.value = currentState.copy(
                    errorMessage = "Pääsalasanan tulee olla vähintään 6 merkkiä pitkä"
                )
                return@launch
            }
            
            if (currentState.masterPassword != currentState.confirmPassword) {
                _uiState.value = currentState.copy(
                    errorMessage = "Salasanat eivät täsmää"
                )
                return@launch
            }
            
            _uiState.value = currentState.copy(isLoading = true)
            
            try {
                val hashedPassword = hashPassword(currentState.masterPassword)
                preferencesManager.setMasterPasswordHash(hashedPassword)
                preferencesManager.setBiometricEnabled(currentState.isBiometricEnabled)
                preferencesManager.setLastUnlockTime(System.currentTimeMillis())
                
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Virhe pääsalasanan tallentamisessa"
                )
            }
        }
    }
    
    fun authenticateWithMasterPassword() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.value = currentState.copy(isLoading = true)
            
            try {
                val hashedPassword = hashPassword(currentState.masterPassword)
                val storedHash = preferencesManager.getMasterPasswordHash()
                
                if (hashedPassword == storedHash) {
                    preferencesManager.setLastUnlockTime(System.currentTimeMillis())
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                } else {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Väärä pääsalasana"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Virhe tunnistautumisessa"
                )
            }
        }
    }
    
    fun authenticateWithBiometric() {
        preferencesManager.setLastUnlockTime(System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(isAuthenticated = true)
    }
    
    fun onBiometricError(error: String) {
        _uiState.value = _uiState.value.copy(errorMessage = error)
    }
    
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }
}

data class AuthUiState(
    val isFirstTime: Boolean = false,
    val masterPassword: String = "",
    val confirmPassword: String = "",
    val isBiometricEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)