package com.sreelakshmims.myspend.domain.repository

import com.sreelakshmims.myspend.data.local.dao.DailySpendingPoint
import com.sreelakshmims.myspend.domain.model.CategorySummary
import com.sreelakshmims.myspend.domain.model.Expense
import com.sreelakshmims.myspend.domain.model.NecessitySummary
import com.sreelakshmims.myspend.domain.model.PaymentMethodSummary
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun insertExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun getExpenseById(id: Long): Expense?
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>>
    fun getTotalSpentBetweenDates(startDate: Long, endDate: Long): Flow<Long>
    fun getCategorySummariesBetweenDates(startDate: Long, endDate: Long): Flow<List<CategorySummary>>
    fun getPaymentMethodSummariesBetweenDates(startDate: Long, endDate: Long): Flow<List<PaymentMethodSummary>>
    fun getNecessitySummariesBetweenDates(startDate: Long, endDate: Long): Flow<List<NecessitySummary>>
    fun getDailySpendingTrend(startDate: Long, endDate: Long): Flow<List<DailySpendingPoint>>
    suspend fun deleteExpensesBetweenDates(startDate: Long, endDate: Long): Int
    suspend fun deleteAllExpenses(): Int
    suspend fun getExpenseCountBetweenDates(startDate: Long, endDate: Long): Int
    suspend fun getTotalExpenseCount(): Int
}
