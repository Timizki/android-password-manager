package com.passwordmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.passwordmanager.presentation.add_edit.AddEditPasswordScreen
import com.passwordmanager.presentation.auth.AuthScreen
import com.passwordmanager.presentation.main.MainScreen
import com.passwordmanager.presentation.navigation.Screen
import com.passwordmanager.ui.theme.PasswordManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PasswordManagerApp()
                }
            }
        }
    }
}

@Composable
fun PasswordManagerApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToAddPassword = {
                    navController.navigate(Screen.AddEditPassword.route)
                },
                onNavigateToEditPassword = { passwordId ->
                    navController.navigate(Screen.AddEditPassword.withArgs(passwordId.toString()))
                }
            )
        }
        
        composable(
            route = Screen.AddEditPassword.route + "?passwordId={passwordId}",
            arguments = listOf(
                navArgument("passwordId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            AddEditPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}