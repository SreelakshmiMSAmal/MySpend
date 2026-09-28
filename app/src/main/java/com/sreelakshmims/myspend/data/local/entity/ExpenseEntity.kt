package com.sreelakshmims.myspend.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [
        Index("expenseDate"),
        Index("categoryId"),
        Index("paymentMethodId")
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountPaise: Long,
    val categoryId: Long,
    val paymentMethodId: Long,
    val expenseDate: Long,
    val merchant: String? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
