package com.sreelakshmims.myspend.data.repository

import com.sreelakshmims.myspend.data.local.dao.CategoryDao
import com.sreelakshmims.myspend.data.local.dao.DailySpendingPoint
import com.sreelakshmims.myspend.data.local.dao.ExpenseDao
import com.sreelakshmims.myspend.data.local.dao.PaymentMethodDao
import com.sreelakshmims.myspend.data.toCategory
import com.sreelakshmims.myspend.data.toEntity
import com.sreelakshmims.myspend.data.toExpense
import com.sreelakshmims.myspend.data.toPaymentMethod
import com.sreelakshmims.myspend.domain.model.CategorySummary
import com.sreelakshmims.myspend.domain.model.Expense
import com.sreelakshmims.myspend.domain.model.NecessitySummary
import com.sreelakshmims.myspend.domain.model.PaymentMethodSummary
import com.sreelakshmims.myspend.domain.repository.ExpenseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val paymentMethodDao: PaymentMethodDao
) : ExpenseRepository {

    override suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        val entity = expenseDao.getExpenseById(id) ?: return null
        val category = categoryDao.getCategoryById(entity.categoryId)?.toCategory() ?: return null
        val paymentMethod = paymentMethodDao.getPaymentMethodById(entity.paymentMethodId)?.toPaymentMethod() ?: return null
        return entity.toExpense(category, paymentMethod)
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().flatMapLatest { entities ->
            combine(
                categoryDao.getAllCategories(),
                paymentMethodDao.getAllPaymentMethods()
            ) { categories, paymentMethods ->
                entities.mapNotNull { entity ->
                    val category = categories.find { it.id == entity.categoryId }?.toCategory()
                    val paymentMethod = paymentMethods.find { it.id == entity.paymentMethodId }?.toPaymentMethod()
                    if (category != null && paymentMethod != null) {
                        entity.toExpense(category, paymentMethod)
                    } else null
                }
            }
        }
    }

    override fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate).flatMapLatest { entities ->
            combine(
                categoryDao.getAllCategories(),
                paymentMethodDao.getAllPaymentMethods()
            ) { categories, paymentMethods ->
                entities.mapNotNull { entity ->
                    val category = categories.find { it.id == entity.categoryId }?.toCategory()
                    val paymentMethod = paymentMethods.find { it.id == entity.paymentMethodId }?.toPaymentMethod()
                    if (category != null && paymentMethod != null) {
                        entity.toExpense(category, paymentMethod)
                    } else null
                }
            }
        }
    }

    override fun getTotalSpentBetweenDates(startDate: Long, endDate: Long): Flow<Long> {
        return expenseDao.getTotalSpentBetweenDates(startDate, endDate).map { it ?: 0L }
    }

    override fun getCategorySummariesBetweenDates(startDate: Long, endDate: Long): Flow<List<CategorySummary>> {
        return expenseDao.getCategoryTotalsBetweenDates(startDate, endDate).flatMapLatest { totals ->
            categoryDao.getAllCategories().map { categories ->
                totals.mapNotNull { total ->
                    val categoryName = categories.find { it.id == total.categoryId }?.name
                    if (categoryName != null) {
                        CategorySummary(categoryName, total.totalAmount)
                    } else null
                }
            }
        }
    }

    override fun getPaymentMethodSummariesBetweenDates(startDate: Long, endDate: Long): Flow<List<PaymentMethodSummary>> {
        return expenseDao.getPaymentMethodTotalsBetweenDates(startDate, endDate).flatMapLatest { totals ->
            paymentMethodDao.getAllPaymentMethods().map { methods ->
                totals.mapNotNull { total ->
                    val methodName = methods.find { it.id == total.paymentMethodId }?.name
                    if (methodName != null) {
                        PaymentMethodSummary(methodName, total.totalAmount)
                    } else null
                }
            }
        }
    }

    override fun getNecessitySummariesBetweenDates(startDate: Long, endDate: Long): Flow<List<NecessitySummary>> {
        return expenseDao.getNecessityTotalsBetweenDates(startDate, endDate).map { totals ->
            totals.map { NecessitySummary(it.necessity, it.totalAmount) }
        }
    }

    override fun getDailySpendingTrend(startDate: Long, endDate: Long): Flow<List<DailySpendingPoint>> {
        return expenseDao.getDailySpendingTrend(startDate, endDate)
    }

    override suspend fun deleteExpensesBetweenDates(startDate: Long, endDate: Long): Int {
        return expenseDao.deleteExpensesBetweenDates(startDate, endDate)
    }

    override suspend fun deleteAllExpenses(): Int {
        return expenseDao.deleteAllExpenses()
    }

    override suspend fun getExpenseCountBetweenDates(startDate: Long, endDate: Long): Int {
        return expenseDao.getExpenseCountBetweenDates(startDate, endDate)
    }

    override suspend fun getTotalExpenseCount(): Int {
        return expenseDao.getTotalExpenseCount()
    }
}
