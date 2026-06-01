package com.example.noteai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.noteai.presentation.AppState
import com.example.noteai.presentation.JourneyLog
import com.example.noteai.presentation.screens.MainScreen
import com.example.noteai.presentation.auth.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    isLightMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    appState: AppState,
    onAppStateChange: (AppState) -> Unit,
    momentum: Int,
    onMomentumChange: (Int) -> Unit,
    logs: List<JourneyLog>,
    onLogsChange: (List<JourneyLog>) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route // Bypass login for this prototype conversion demo
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { /* Demo only */ },
                onLoginSuccess = { userName ->
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                userName = "Mahasiswa",
                isLightMode = isLightMode,
                onThemeToggle = onThemeToggle,
                mentalState = appState,
                onMentalStateChange = onAppStateChange,
                momentum = momentum,
                onMomentumChange = onMomentumChange,
                logs = logs,
                onLogsChange = onLogsChange,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
