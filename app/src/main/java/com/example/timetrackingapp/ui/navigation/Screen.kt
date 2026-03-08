package com.example.timetrackingapp.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object ProjectList : Screen("project_list")
    object Timer : Screen("timer/{projectId}/{projectName}") {
        fun createRoute(projectId: String, projectName: String) = "timer/$projectId/$projectName"
    }
    object History : Screen("history")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}