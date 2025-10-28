package com.passwordmanager.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.passwordmanager.utils.BiometricAuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val biometricManager = remember { BiometricAuthManager() }
    
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Salasanahallinta",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (uiState.isFirstTime) {
            SetupMasterPasswordContent(
                uiState = uiState,
                showPassword = showPassword,
                showConfirmPassword = showConfirmPassword,
                onShowPasswordToggle = { showPassword = !showPassword },
                onShowConfirmPasswordToggle = { showConfirmPassword = !showConfirmPassword },
                onMasterPasswordChanged = viewModel::onMasterPasswordChanged,
                onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
                onBiometricToggled = viewModel::onBiometricToggled,
                onSetupClick = viewModel::setupMasterPassword,
                biometricAvailable = biometricManager.isBiometricAvailable(context)
            )
        } else {
            LoginContent(
                uiState = uiState,
                showPassword = showPassword,
                onShowPasswordToggle = { showPassword = !showPassword },
                onMasterPasswordChanged = viewModel::onMasterPasswordChanged,
                onLoginClick = viewModel::authenticateWithMasterPassword,
                onBiometricClick = {
                    if (context is FragmentActivity) {
                        biometricManager.authenticateWithBiometric(
                            activity = context,
                            onSuccess = viewModel::authenticateWithBiometric,
                            onError = viewModel::onBiometricError
                        )
                    }
                },
                biometricEnabled = uiState.isBiometricEnabled && biometricManager.isBiometricAvailable(context)
            )
        }
        
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = uiState.errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun SetupMasterPasswordContent(
    uiState: AuthUiState,
    showPassword: Boolean,
    showConfirmPassword: Boolean,
    onShowPasswordToggle: () -> Unit,
    onShowConfirmPasswordToggle: () -> Unit,
    onMasterPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onBiometricToggled: (Boolean) -> Unit,
    onSetupClick: () -> Unit,
    biometricAvailable: Boolean
) {
    Text(
        text = "Luo pääsalasana",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    OutlinedTextField(
        value = uiState.masterPassword,
        onValueChange = onMasterPasswordChanged,
        label = { Text("Pääsalasana") },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onShowPasswordToggle) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showPassword) "Piilota salasana" else "Näytä salasana"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = uiState.confirmPassword,
        onValueChange = onConfirmPasswordChanged,
        label = { Text("Vahvista pääsalasana") },
        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onShowConfirmPasswordToggle) {
                Icon(
                    imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showConfirmPassword) "Piilota salasana" else "Näytä salasana"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    
    if (biometricAvailable) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.isBiometricEnabled,
                onCheckedChange = onBiometricToggled
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Käytä biometristä tunnistautumista")
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    
    Button(
        onClick = onSetupClick,
        enabled = !uiState.isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Luo pääsalasana")
        }
    }
}

@Composable
private fun LoginContent(
    uiState: AuthUiState,
    showPassword: Boolean,
    onShowPasswordToggle: () -> Unit,
    onMasterPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBiometricClick: () -> Unit,
    biometricEnabled: Boolean
) {
    Text(
        text = "Syötä pääsalasana",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    OutlinedTextField(
        value = uiState.masterPassword,
        onValueChange = onMasterPasswordChanged,
        label = { Text("Pääsalasana") },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onShowPasswordToggle) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showPassword) "Piilota salasana" else "Näytä salasana"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    Button(
        onClick = onLoginClick,
        enabled = !uiState.isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Kirjaudu sisään")
        }
    }
    
    if (biometricEnabled) {
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onBiometricClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Käytä biometristä tunnistautumista")
        }
    }
}