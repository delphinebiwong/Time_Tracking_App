 package com.example.timetrackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.timetrackingapp.ui.auth.LoginScreen
import com.example.timetrackingapp.ui.history.HistoryScreen
import com.example.timetrackingapp.ui.navigation.Screen
import com.example.timetrackingapp.ui.project.ProjectListScreen
import com.example.timetrackingapp.ui.reports.ReportsScreen
import com.example.timetrackingapp.ui.settings.SettingsScreen
import com.example.timetrackingapp.ui.splash.SplashScreen
import com.example.timetrackingapp.ui.theme.TimeTrackingAppTheme
import com.example.timetrackingapp.ui.timer.TimerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeTrackingAppTheme {
                TimeTrackingAppNavHost()
            }
        }
    }
}

@Composable
fun TimeTrackingAppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.ProjectList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.ProjectList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProjectList.route) {
            ProjectListScreen(
                onNavigateToTimer = { id, name ->
                    navController.navigate(Screen.Timer.createRoute(id, name))
                },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.Timer.route,
            arguments = listOf(
                navArgument("projectId") { defaultValue = "" },
                navArgument("projectName") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val projectName = backStackEntry.arguments?.getString("projectName") ?: ""
            TimerScreen(
                projectId = projectId,
                projectName = projectName,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToProjects = { navController.navigate(Screen.ProjectList.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(
                onNavigateToProjects = { navController.navigate(Screen.ProjectList.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToProjects = { navController.navigate(Screen.ProjectList.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}