package com.passwordmanager.autofill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.autofill.AutofillManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.passwordmanager.domain.repository.PasswordProfileRepository
import com.passwordmanager.ui.theme.PasswordManagerTheme
import com.passwordmanager.utils.PassToolGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AutofillActivity : ComponentActivity() {
    
    @Inject
    lateinit var repository: PasswordProfileRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val profileId = intent.getLongExtra(PasswordManagerAutofillService.EXTRA_PROFILE_ID, -1L)
        
        if (profileId == -1L) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
        
        setContent {
            PasswordManagerTheme {
                AutofillScreen(
                    profileId = profileId,
                    onPasswordGenerated = { username, password ->
                        returnAutofillResult(username, password)
                    },
                    onCancel = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AutofillScreen(
        profileId: Long,
        onPasswordGenerated: (String, String) -> Unit,
        onCancel: () -> Unit
    ) {
        var profile by remember { mutableStateOf<com.passwordmanager.domain.model.PasswordProfile?>(null) }
        var passphrase by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        
        val keyboardController = LocalSoftwareKeyboardController.current
        
        // Lataa profiili
        LaunchedEffect(profileId) {
            try {
                profile = repository.getProfileById(profileId)
                if (profile == null) {
                    error = "Profiilia ei löytynyt"
                }
            } catch (e: Exception) {
                error = e.message ?: "Virhe ladattaessa profiilia"
            }
        }
        
        val generatePassword = {
            if (passphrase.isNotBlank() && profile != null) {
                isLoading = true
                lifecycleScope.launch {
                    try {
                        val password = PassToolGenerator.generatePassword(
                            passphrase = passphrase,
                            length = profile!!.passwordLength
                        )
                        onPasswordGenerated(profile!!.username, password)
                    } catch (e: Exception) {
                        error = e.message ?: "Virhe generoitaessa salasanaa"
                        isLoading = false
                    }
                }
            }
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Autofill - ${profile?.title ?: "Ladataan..."}") }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                profile?.let { prof ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Syötä passphrase generoidaksesi salasanan:",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            if (prof.website.isNotEmpty()) {
                                Text(
                                    text = "Sivusto: ${prof.website}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            if (prof.username.isNotEmpty()) {
                                Text(
                                    text = "Käyttäjänimi: ${prof.username}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            OutlinedTextField(
                                value = passphrase,
                                onValueChange = { 
                                    passphrase = it
                                    error = null
                                },
                                label = { Text("Passphrase") },
                                visualTransformation = if (isPasswordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) {
                                                Icons.Default.VisibilityOff
                                            } else {
                                                Icons.Default.Visibility
                                            },
                                            contentDescription = if (isPasswordVisible) {
                                                "Piilota passphrase"
                                            } else {
                                                "Näytä passphrase"
                                            }
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        generatePassword()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                            
                            error?.let { errorMsg ->
                                Text(
                                    text = errorMsg,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onCancel,
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading
                                ) {
                                    Text("Peruuta")
                                }
                                
                                Button(
                                    onClick = generatePassword,
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading && passphrase.isNotBlank()
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text("Täytä kentät")
                                }
                            }
                        }
                    }
                } ?: run {
                    if (error != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = error!!,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(onClick = onCancel) {
                            Text("Sulje")
                        }
                    } else {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Ladataan profiilia...")
                    }
                }
            }
        }
    }
    
    private fun returnAutofillResult(username: String, password: String) {
        // Palauta tiedot autofill-palvelulle
        val replyIntent = Intent().apply {
            putExtra("username", username)
            putExtra("password", password)
        }
        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }
}