package com.sreelakshmims.myspend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sreelakshmims.myspend.presentation.add_expense.AddExpenseScreen
import com.sreelakshmims.myspend.presentation.analytics.AnalyticsScreen
import com.sreelakshmims.myspend.presentation.dashboard.DashboardScreen
import com.sreelakshmims.myspend.presentation.settings.SettingsScreen
import com.sreelakshmims.myspend.presentation.transactions.TransactionsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense.route)
                }
            )
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Transactions.route) { TransactionsScreen() }
        composable(Screen.Analytics.route) { AnalyticsScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
