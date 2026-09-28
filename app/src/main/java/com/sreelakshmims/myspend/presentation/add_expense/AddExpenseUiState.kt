package com.sreelakshmims.myspend.presentation.add_expense

import com.sreelakshmims.myspend.domain.model.Category
import com.sreelakshmims.myspend.domain.model.PaymentMethod

data class AddExpenseUiState(
    val amount: String = "",
    val categories: List<Category> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val date: Long = System.currentTimeMillis(),
    val merchant: String = "",
    val note: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
