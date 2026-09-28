package com.sreelakshmims.myspend.presentation.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object AddExpense : Screen("add_expense")
}
