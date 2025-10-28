package com.passwordmanager.presentation.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Main : Screen("main")
    object AddEditPassword : Screen("add_edit_password")
    object PasswordGenerator : Screen("password_generator")
    
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}