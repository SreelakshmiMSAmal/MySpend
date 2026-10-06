package com.sreelakshmims.myspend.data.local.dao

import androidx.room.*
import com.sreelakshmims.myspend.data.local.entity.ExpenseEntity
import com.sreelakshmims.myspend.domain.model.NecessityLevel
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate ORDER BY expenseDate DESC")
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate")
    fun getTotalSpentBetweenDates(startDate: Long, endDate: Long): Flow<Long?>

    @Query("SELECT categoryId, SUM(amountPaise) as totalAmount FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate GROUP BY categoryId")
    fun getCategoryTotalsBetweenDates(startDate: Long, endDate: Long): Flow<List<CategoryTotal>>

    @Query("SELECT paymentMethodId, SUM(amountPaise) as totalAmount FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate GROUP BY paymentMethodId")
    fun getPaymentMethodTotalsBetweenDates(startDate: Long, endDate: Long): Flow<List<PaymentMethodTotal>>

    @Query("SELECT c.necessity as necessity, SUM(e.amountPaise) as totalAmount FROM expenses e INNER JOIN categories c ON e.categoryId = c.id WHERE e.expenseDate BETWEEN :startDate AND :endDate GROUP BY c.necessity")
    fun getNecessityTotalsBetweenDates(startDate: Long, endDate: Long): Flow<List<NecessityTotal>>

    @Query("SELECT expenseDate / 86400000 * 86400000 as date, SUM(amountPaise) as totalAmount FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date ASC")
    fun getDailySpendingTrend(startDate: Long, endDate: Long): Flow<List<DailySpendingPoint>>

    @Query("DELETE FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate")
    suspend fun deleteExpensesBetweenDates(startDate: Long, endDate: Long): Int

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses(): Int

    @Query("SELECT COUNT(*) FROM expenses WHERE expenseDate BETWEEN :startDate AND :endDate")
    suspend fun getExpenseCountBetweenDates(startDate: Long, endDate: Long): Int

    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun getTotalExpenseCount(): Int
}

data class CategoryTotal(
    val categoryId: Long,
    val totalAmount: Long
)

data class PaymentMethodTotal(
    val paymentMethodId: Long,
    val totalAmount: Long
)

data class NecessityTotal(
    val necessity: NecessityLevel,
    val totalAmount: Long
)

data class DailySpendingPoint(
    val date: Long,
    val totalAmount: Long
)
