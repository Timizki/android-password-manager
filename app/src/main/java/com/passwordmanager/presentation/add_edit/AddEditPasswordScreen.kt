package com.passwordmanager.presentation.add_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPassword by remember { mutableStateOf(false) }
    var showPasswordGenerator by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (uiState.isEditMode) "Muokkaa salasanaa" else "Lisää salasana") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Takaisin")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Otsikko *") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.website,
                onValueChange = viewModel::onWebsiteChanged,
                label = { Text("Verkkosivusto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChanged,
                label = { Text("Käyttäjänimi") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text("Salasana *") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Row {
                        IconButton(onClick = { showPasswordGenerator = !showPasswordGenerator }) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Luo salasana")
                        }
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showPassword) "Piilota salasana" else "Näytä salasana"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (showPasswordGenerator) {
                PasswordGeneratorSection(
                    uiState = uiState,
                    onPasswordLengthChanged = viewModel::onPasswordLengthChanged,
                    onIncludeUppercaseChanged = viewModel::onIncludeUppercaseChanged,
                    onIncludeLowercaseChanged = viewModel::onIncludeLowercaseChanged,
                    onIncludeNumbersChanged = viewModel::onIncludeNumbersChanged,
                    onIncludeSymbolsChanged = viewModel::onIncludeSymbolsChanged,
                    onGeneratePassword = viewModel::generatePassword
                )
            }
            
            OutlinedTextField(
                value = uiState.category,
                onValueChange = viewModel::onCategoryChanged,
                label = { Text("Kategoria") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChanged,
                label = { Text("Muistiinpanot") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (uiState.errorMessage != null) {
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
            
            Button(
                onClick = { viewModel.savePassword(onNavigateBack) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Tallenna")
                }
            }
        }
    }
}

@Composable
private fun PasswordGeneratorSection(
    uiState: AddEditPasswordUiState,
    onPasswordLengthChanged: (Int) -> Unit,
    onIncludeUppercaseChanged: (Boolean) -> Unit,
    onIncludeLowercaseChanged: (Boolean) -> Unit,
    onIncludeNumbersChanged: (Boolean) -> Unit,
    onIncludeSymbolsChanged: (Boolean) -> Unit,
    onGeneratePassword: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Salasanageneraattori",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text("Pituus: ${uiState.passwordLength}")
            Slider(
                value = uiState.passwordLength.toFloat(),
                onValueChange = { onPasswordLengthChanged(it.toInt()) },
                valueRange = 4f..50f,
                steps = 45
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.includeUppercase,
                    onCheckedChange = onIncludeUppercaseChanged
                )
                Text("Isot kirjaimet (A-Z)")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.includeLowercase,
                    onCheckedChange = onIncludeLowercaseChanged
                )
                Text("Pienet kirjaimet (a-z)")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.includeNumbers,
                    onCheckedChange = onIncludeNumbersChanged
                )
                Text("Numerot (0-9)")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.includeSymbols,
                    onCheckedChange = onIncludeSymbolsChanged
                )
                Text("Symbolit (!@#$...)")
            }
            
            Button(
                onClick = onGeneratePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Luo salasana")
            }
        }
    }
}