package com.sreelakshmims.myspend.domain.usecase

import com.sreelakshmims.myspend.domain.model.Expense
import com.sreelakshmims.myspend.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        if (expense.amountPaise <= 0) {
            throw IllegalArgumentException("Amount must be greater than zero")
        }
        repository.insertExpense(expense)
    }
}
