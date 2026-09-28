package com.sreelakshmims.myspend.presentation.transactions

import com.sreelakshmims.myspend.domain.model.Category
import com.sreelakshmims.myspend.domain.model.Expense

data class TransactionsUiState(
    val groupedExpenses: Map<String, List<Expense>> = emptyMap(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
