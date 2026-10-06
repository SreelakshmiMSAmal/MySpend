package com.sreelakshmims.myspend.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreelakshmims.myspend.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        // Today range: 00:00:00.000 to 23:59:59.999 of today
        val calTodayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = calTodayStart.timeInMillis

        val calTodayEnd = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endOfToday = calTodayEnd.timeInMillis

        // This Week range: start of first day of week to end of current week
        val calWeekStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        val startOfWeek = calWeekStart.timeInMillis

        val calWeekEnd = (calWeekStart.clone() as Calendar).apply {
            add(Calendar.DAY_OF_WEEK, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endOfWeek = calWeekEnd.timeInMillis

        // This Month range: 1st day 00:00:00.000 to last day 23:59:59.999 of current month
        val calMonthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = calMonthStart.timeInMillis

        val calMonthEnd = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endOfMonth = calMonthEnd.timeInMillis

        val daysInMonthPassed = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        combine(
            expenseRepository.getTotalSpentBetweenDates(startOfToday, endOfToday),
            expenseRepository.getTotalSpentBetweenDates(startOfWeek, endOfWeek),
            expenseRepository.getTotalSpentBetweenDates(startOfMonth, endOfMonth),
            expenseRepository.getAllExpenses()
        ) { today, week, month, allExpenses ->
            DashboardUiState(
                spentToday = today,
                spentThisWeek = week,
                totalSpentMonth = month,
                averagePerDay = if (daysInMonthPassed > 0) month / daysInMonthPassed else month,
                recentExpenses = allExpenses.take(5),
                isLoading = false
            )
        }.onEach { newState ->
            _uiState.update { newState }
        }.launchIn(viewModelScope)
    }
}
