package com.vazdautsan.drwebtesttask.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vazdautsan.drwebtesttask.presentation.details.AppDetailsRoute
import com.vazdautsan.drwebtesttask.presentation.list.AppListRoute
import kotlinx.serialization.Serializable

@Serializable
object AppListDestination

@Serializable
data class AppDetailsDestination(val packageName: String)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppListDestination) {
        composable<AppListDestination> {
            AppListRoute(onOpenDetails = { navController.navigate(AppDetailsDestination(it)) })
        }
        composable<AppDetailsDestination> {
            AppDetailsRoute(onBack = { navController.navigateUp() })
        }
    }
}
