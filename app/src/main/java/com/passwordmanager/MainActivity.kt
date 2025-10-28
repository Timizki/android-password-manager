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
import com.passwordmanager.presentation.profile.ProfileListScreen
import com.passwordmanager.presentation.profile.ProfileDetailScreen
import com.passwordmanager.presentation.profile.AddEditProfileScreen
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
        startDestination = "profile_list"
    ) {
        composable("profile_list") {
            ProfileListScreen(
                onNavigateToAddProfile = {
                    navController.navigate("add_profile")
                },
                onNavigateToProfile = { profileId ->
                    navController.navigate("profile_detail/$profileId")
                }
            )
        }
        
        composable("add_profile") {
            AddEditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "edit_profile/{profileId}",
            arguments = listOf(
                navArgument("profileId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: 0L
            AddEditProfileScreen(
                profileId = profileId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "profile_detail/{profileId}",
            arguments = listOf(
                navArgument("profileId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getLong("profileId") ?: 0L
            ProfileDetailScreen(
                profileId = profileId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate("edit_profile/$id")
                }
            )
        }
    }
}