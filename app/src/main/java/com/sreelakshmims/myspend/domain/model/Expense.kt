package com.sreelakshmims.myspend.domain.model

data class Expense(
    val id: Long = 0,
    val amountPaise: Long,
    val category: Category,
    val paymentMethod: PaymentMethod,
    val expenseDate: Long,
    val merchant: String? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
