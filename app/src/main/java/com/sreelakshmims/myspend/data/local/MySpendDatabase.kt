package com.sreelakshmims.myspend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sreelakshmims.myspend.data.local.dao.CategoryDao
import com.sreelakshmims.myspend.data.local.dao.ExpenseDao
import com.sreelakshmims.myspend.data.local.dao.PaymentMethodDao
import com.sreelakshmims.myspend.data.local.entity.CategoryEntity
import com.sreelakshmims.myspend.data.local.entity.ExpenseEntity
import com.sreelakshmims.myspend.data.local.entity.PaymentMethodEntity

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        PaymentMethodEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MySpendDatabase : RoomDatabase() {
    abstract val expenseDao: ExpenseDao
    abstract val categoryDao: CategoryDao
    abstract val paymentMethodDao: PaymentMethodDao

    companion object {
        const val DATABASE_NAME = "myspend_db"
    }
}
