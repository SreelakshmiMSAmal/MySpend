package com.sreelakshmims.myspend.presentation.dashboard

import com.sreelakshmims.myspend.domain.model.Expense

data class DashboardUiState(
    val totalSpentMonth: Long = 0,
    val spentToday: Long = 0,
    val spentThisWeek: Long = 0,
    val averagePerDay: Long = 0,
    val recentExpenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
