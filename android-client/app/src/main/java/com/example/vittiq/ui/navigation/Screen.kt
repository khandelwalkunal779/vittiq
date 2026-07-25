package com.example.vittiq.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Overalls : Screen("overalls", "Overalls", Icons.Default.Home)
    object Logs : Screen("logs", "Logs", Icons.AutoMirrored.Filled.List)
    object AiInsights : Screen("ai_insights", "AI Insights", Icons.Default.Star)
    object Shared : Screen("shared", "Shared", Icons.Default.Share)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    companion object {
        val bottomNavItems = listOf(Overalls, Logs, AiInsights, Shared, Profile)
    }
}
