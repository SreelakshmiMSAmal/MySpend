package com.sreelakshmims.myspend.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sreelakshmims.myspend.data.local.dao.DailySpendingPoint
import com.sreelakshmims.myspend.domain.model.CategorySummary
import com.sreelakshmims.myspend.domain.model.Expense
import com.sreelakshmims.myspend.domain.model.PaymentMethodSummary
import com.sreelakshmims.myspend.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private val _selectedPeriodType = MutableStateFlow(PeriodType.MONTH)

    val uiState: StateFlow<AnalyticsUiState> = combine(
        _selectedMonth,
        _selectedYear,
        _selectedPeriodType
    ) { month, year, periodType ->
        Triple(month, year, periodType)
    }.flatMapLatest { (month, year, periodType) ->
        val (startTime, endTime) = getDateRange(month, year, periodType)
        val (prevStartTime, prevEndTime) = getPreviousDateRange(month, year, periodType)

        combine(
            expenseRepository.getTotalSpentBetweenDates(startTime, endTime),
            expenseRepository.getCategorySummariesBetweenDates(startTime, endTime),
            expenseRepository.getPaymentMethodSummariesBetweenDates(startTime, endTime),
            expenseRepository.getDailySpendingTrend(startTime, endTime),
            expenseRepository.getTotalSpentBetweenDates(prevStartTime, prevEndTime),
            expenseRepository.getExpensesBetweenDates(startTime, endTime)
        ) { args ->
            val total = args[0] as Long
            val categories = args[1] as List<CategorySummary>
            val methods = args[2] as List<PaymentMethodSummary>
            val trend = args[3] as List<DailySpendingPoint>
            val prevTotal = args[4] as Long
            val expenses = args[5] as List<Expense>

            val highestDay = trend.maxByOrNull { it.totalAmount }
            val insights = mutableListOf<SpendingInsight>()

            categories.maxByOrNull { it.totalAmountPaise }?.let { maxCat ->
                val percentage = if (total > 0) (maxCat.totalAmountPaise.toFloat() / total * 100).toInt() else 0
                insights.add(
                    SpendingInsight(
                        title = "${maxCat.categoryName} is your highest spending category",
                        description = "Taking up $percentage% of your total outflow.",
                        type = InsightType.CATEGORY
                    )
                )
            }

            if (prevTotal > 0) {
                val diff = total - prevTotal
                val percentage = (diff.toFloat() / prevTotal * 100).toInt()
                val direction = if (diff >= 0) "increased by +$percentage%" else "decreased by $percentage%"
                insights.add(
                    SpendingInsight(
                        title = "Spending $direction",
                        description = "Compared to previous period.",
                        type = InsightType.TREND
                    )
                )
            }

            AnalyticsUiState(
                selectedMonth = month,
                selectedYear = year,
                selectedPeriodType = periodType,
                totalSpent = total,
                previousMonthTotal = prevTotal,
                categorySummaries = categories,
                paymentMethodSummaries = methods,
                dailyTrend = trend,
                transactionCount = expenses.size,
                highestDayAmount = highestDay?.totalAmount ?: 0L,
                highestDayDate = highestDay?.date ?: 0L,
                insights = insights,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )

    fun onMonthChange(month: Int) {
        _selectedMonth.value = month
    }

    fun onYearChange(year: Int) {
        _selectedYear.value = year
    }

    fun onMonthYearSelected(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
    }

    fun onPeriodTypeChange(type: PeriodType) {
        _selectedPeriodType.value = type
    }

    private fun getDateRange(month: Int, year: Int, periodType: PeriodType): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)

        return if (periodType == PeriodType.MONTH) {
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startTime = calendar.timeInMillis

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endTime = calendar.timeInMillis

            Pair(startTime, endTime)
        } else {
            calendar.set(Calendar.MONTH, Calendar.JANUARY)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startTime = calendar.timeInMillis

            calendar.set(Calendar.MONTH, Calendar.DECEMBER)
            calendar.set(Calendar.DAY_OF_MONTH, 31)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endTime = calendar.timeInMillis

            Pair(startTime, endTime)
        }
    }

    private fun getPreviousDateRange(month: Int, year: Int, periodType: PeriodType): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.clear()

        return if (periodType == PeriodType.MONTH) {
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.add(Calendar.MONTH, -1)

            val prevMonth = calendar.get(Calendar.MONTH)
            val prevYear = calendar.get(Calendar.YEAR)
            getDateRange(prevMonth, prevYear, PeriodType.MONTH)
        } else {
            getDateRange(month, year - 1, PeriodType.YEAR)
        }
    }
}