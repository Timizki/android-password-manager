package com.passwordmanager.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProfileScreen(
    profileId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddEditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing = profileId != null
    
    LaunchedEffect(profileId) {
        if (profileId != null) {
            viewModel.loadProfile(profileId)
        }
    }
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Muokkaa profiilia" else "Lisää profiili") },
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
            // Perustiedot
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Perustiedot",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = viewModel::updateTitle,
                        label = { Text("Otsikko *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.titleError != null,
                        supportingText = uiState.titleError?.let { { Text(it) } }
                    )
                    
                    OutlinedTextField(
                        value = uiState.website,
                        onValueChange = viewModel::updateWebsite,
                        label = { Text("Sivusto") },
                        placeholder = { Text("esim. google.com") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = viewModel::updateUsername,
                        label = { Text("Käyttäjänimi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = uiState.category,
                        onValueChange = viewModel::updateCategory,
                        label = { Text("Kategoria") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Salasana-asetukset
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Salasana-asetukset",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = uiState.passwordLength.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { viewModel.updatePasswordLength(it) }
                        },
                        label = { Text("Salasanan pituus") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.passwordLengthError != null,
                        supportingText = uiState.passwordLengthError?.let { { Text(it) } }
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Käytä erikoismerkkejä",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = uiState.useSpecialChars,
                            onCheckedChange = viewModel::updateUseSpecialChars
                        )
                    }
                    
                    if (uiState.useSpecialChars) {
                        OutlinedTextField(
                            value = uiState.specialChars,
                            onValueChange = viewModel::updateSpecialChars,
                            label = { Text("Erikoismerkit") },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = { 
                                Text("Määritä mitkä erikoismerkit salasanassa saa olla") 
                            }
                        )
                    }
                }
            }
            
            // Muistiinpanot
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Muistiinpanot",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::updateNotes,
                        label = { Text("Muistiinpanot") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }
            
            // Tallenna-painike
            Button(
                onClick = viewModel::saveProfile,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.title.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isEditing) "Tallenna muutokset" else "Lisää profiili")
            }
            
            // Virheviesti
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}