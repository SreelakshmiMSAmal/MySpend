package com.sreelakshmims.myspend.data

import com.sreelakshmims.myspend.data.local.entity.CategoryEntity
import com.sreelakshmims.myspend.data.local.entity.ExpenseEntity
import com.sreelakshmims.myspend.data.local.entity.PaymentMethodEntity
import com.sreelakshmims.myspend.domain.model.Category
import com.sreelakshmims.myspend.domain.model.Expense
import com.sreelakshmims.myspend.domain.model.PaymentMethod

fun CategoryEntity.toCategory(): Category {
    return Category(id, name, icon, color, isDefault)
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(id, name, icon, color, isDefault)
}

fun PaymentMethodEntity.toPaymentMethod(): PaymentMethod {
    return PaymentMethod(id, name, isDefault)
}

fun PaymentMethod.toEntity(): PaymentMethodEntity {
    return PaymentMethodEntity(id, name, isDefault)
}

fun ExpenseEntity.toExpense(category: Category, paymentMethod: PaymentMethod): Expense {
    return Expense(
        id = id,
        amountPaise = amountPaise,
        category = category,
        paymentMethod = paymentMethod,
        expenseDate = expenseDate,
        merchant = merchant,
        note = note,
        createdAt = createdAt
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amountPaise = amountPaise,
        categoryId = category.id,
        paymentMethodId = paymentMethod.id,
        expenseDate = expenseDate,
        merchant = merchant,
        note = note,
        createdAt = createdAt
    )
}
