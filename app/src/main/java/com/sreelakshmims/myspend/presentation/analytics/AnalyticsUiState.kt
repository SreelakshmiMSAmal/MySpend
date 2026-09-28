package com.sreelakshmims.myspend.presentation.analytics

import com.sreelakshmims.myspend.data.local.dao.DailySpendingPoint
import com.sreelakshmims.myspend.domain.model.CategorySummary
import com.sreelakshmims.myspend.domain.model.PaymentMethodSummary
import java.util.Calendar

data class AnalyticsUiState(
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val totalSpent: Long = 0,
    val previousMonthTotal: Long = 0,
    val monthlyLimit: Long = 3000000, // Default 30,000 paise * 100 = 30,000.00? No, paise is cent. 30,000 Rs = 3,000,000 paise.
    val categorySummaries: List<CategorySummary> = emptyList(),
    val paymentMethodSummaries: List<PaymentMethodSummary> = emptyList(),
    val dailyTrend: List<DailySpendingPoint> = emptyList(),
    val transactionCount: Int = 0,
    val highestDayAmount: Long = 0,
    val highestDayDate: Long = 0,
    val insights: List<SpendingInsight> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPeriodType: PeriodType = PeriodType.MONTH
)

enum class PeriodType {
    MONTH, YEAR
}

data class SpendingInsight(
    val title: String,
    val description: String,
    val type: InsightType
)

enum class InsightType {
    CATEGORY, PAYMENT_METHOD, TREND, SPIKE
}
