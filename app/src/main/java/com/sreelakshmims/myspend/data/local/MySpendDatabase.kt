package com.sreelakshmims.myspend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MySpendDatabase : RoomDatabase() {
    abstract val expenseDao: ExpenseDao
    abstract val categoryDao: CategoryDao
    abstract val paymentMethodDao: PaymentMethodDao

    companion object {
        const val DATABASE_NAME = "myspend_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val newCategories = listOf(
                    "EMI",
                    "Savings",
                    "Insurance",
                    "Parents",
                    "Charity",
                    "Home Maintenance",
                    "Professional",
                    "Fuel & Vehicle",
                    "Gifts & Celebrations"
                )
                for (category in newCategories) {
                    db.execSQL(
                        "INSERT OR IGNORE INTO categories (name, isDefault) VALUES ('$category', 1)"
                    )
                }
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE categories ADD COLUMN necessity TEXT NOT NULL DEFAULT 'NECESSITY'")

                db.execSQL("UPDATE categories SET necessity = 'NECESSITY' WHERE name IN ('EMI', 'Fuel & Vehicle', 'Parents', 'Insurance', 'Food & Dining', 'Food', 'Groceries', 'Transport', 'Rent', 'Bills & Utilities', 'Bills')")
                db.execSQL("UPDATE categories SET necessity = 'SEMI_NECESSITY' WHERE name IN ('Home Maintenance', 'Professional', 'Health & Medical', 'Health', 'Education')")
                db.execSQL("UPDATE categories SET necessity = 'SAVINGS' WHERE name IN ('Savings')")
                db.execSQL("UPDATE categories SET necessity = 'NOT_NECESSARY' WHERE name IN ('Gifts & Celebrations', 'Charity', 'Shopping', 'Entertainment', 'Subscriptions', 'Travel', 'Personal', 'Others', 'Other')")
            }
        }
    }
}
