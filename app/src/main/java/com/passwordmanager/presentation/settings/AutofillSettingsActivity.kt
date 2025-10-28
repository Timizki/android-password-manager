package com.passwordmanager.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.passwordmanager.ui.theme.PasswordManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutofillSettingsActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            PasswordManagerTheme {
                AutofillSettingsScreen(
                    onNavigateBack = { finish() }
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AutofillSettingsScreen(
        onNavigateBack: () -> Unit
    ) {
        val context = LocalContext.current
        val autofillManager = context.getSystemService(AutofillManager::class.java)
        
        var isAutofillEnabled by remember { 
            mutableStateOf(autofillManager?.hasEnabledAutofillServices() == true)
        }
        
        // Päivitä tila kun palataan asetuksista
        LaunchedEffect(Unit) {
            isAutofillEnabled = autofillManager?.hasEnabledAutofillServices() == true
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Autofill-asetukset") },
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Autofill-tila kortti
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Autofill-palvelu",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            if (isAutofillEnabled) {
                                AssistChip(
                                    onClick = { },
                                    label = { Text("Käytössä") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            } else {
                                AssistChip(
                                    onClick = { },
                                    label = { Text("Ei käytössä") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                )
                            }
                        }
                        
                        Text(
                            text = if (isAutofillEnabled) {
                                "Salasanahallinta voi täyttää salasanoja automaattisesti muissa sovelluksissa."
                            } else {
                                "Ota autofill käyttöön täyttääksesi salasanoja automaattisesti muissa sovelluksissa."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (!isAutofillEnabled) {
                            Button(
                                onClick = {
                                    // Avaa Android-asetukset autofill-palveluille
                                    val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
                                        data = android.net.Uri.parse("package:${context.packageName}")
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Siirry asetuksiin")
                            }
                        }
                    }
                }
                
                // Ohje-kortti
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Miten autofill toimii?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Text(
                            text = "1. Ota autofill käyttöön Android-asetuksista",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "2. Kun kirjaudut sovellukseen tai sivustolle, näet profiilivaihtoehdot",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "3. Valitse profiili ja syötä passphrase",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "4. Salasana generoidaan ja täytetään automaattisesti",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Turvallisuus-kortti
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🔒 Turvallisuus",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Text(
                            text = "• Salasanoja ei tallenneta - ne generoidaan tarvittaessa",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "• Passphrase pysyy vain muistissa generoinnin ajan",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "• PassTool-yhteensopiva deterministinen generointi",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}