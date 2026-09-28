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
        val calendar = Calendar.getInstance()
        
        // Today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis
        val endOfToday = startOfToday + 86400000 - 1

        // This Week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = calendar.timeInMillis
        val endOfWeek = System.currentTimeMillis()

        // This Month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = calendar.timeInMillis
        val endOfMonth = System.currentTimeMillis()
        
        val daysInMonthPassed = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        combine(
            expenseRepository.getTotalSpentBetweenDates(startOfToday, endOfToday),
            expenseRepository.getTotalSpentBetweenDates(startOfWeek, endOfWeek),
            expenseRepository.getTotalSpentBetweenDates(startOfMonth, endOfMonth),
            expenseRepository.getAllExpenses() // We could limit this in repo if needed
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
