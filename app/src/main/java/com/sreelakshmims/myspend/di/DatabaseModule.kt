package com.sreelakshmims.myspend.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sreelakshmims.myspend.data.local.MySpendDatabase
import com.sreelakshmims.myspend.data.local.dao.CategoryDao
import com.sreelakshmims.myspend.data.local.dao.ExpenseDao
import com.sreelakshmims.myspend.data.local.dao.PaymentMethodDao
import com.sreelakshmims.myspend.data.local.entity.CategoryEntity
import com.sreelakshmims.myspend.data.local.entity.PaymentMethodEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMySpendDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>,
        paymentMethodDaoProvider: Provider<PaymentMethodDao>
    ): MySpendDatabase {
        return Room.databaseBuilder(
            context,
            MySpendDatabase::class.java,
            MySpendDatabase.DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateCategories(categoryDaoProvider.get())
                    prepopulatePaymentMethods(paymentMethodDaoProvider.get())
                }
            }
        }).build()
    }

    private suspend fun prepopulateCategories(dao: CategoryDao) {
        val categories = listOf(
            "Food", "Groceries", "Transport", "Shopping", "Bills",
            "Rent", "Health", "Entertainment", "Education", "Travel",
            "Subscriptions", "Personal", "Others"
        ).map { CategoryEntity(name = it, isDefault = true) }
        categories.forEach { dao.insertCategory(it) }
    }

    private suspend fun prepopulatePaymentMethods(dao: PaymentMethodDao) {
        val paymentMethods = listOf(
            "Cash", "UPI", "Debit Card", "Credit Card",
            "Bank Transfer", "Net Banking", "Other"
        ).map { PaymentMethodEntity(name = it, isDefault = true) }
        paymentMethods.forEach { dao.insertPaymentMethod(it) }
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: MySpendDatabase): ExpenseDao {
        return database.expenseDao
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: MySpendDatabase): CategoryDao {
        return database.categoryDao
    }

    @Provides
    @Singleton
    fun providePaymentMethodDao(database: MySpendDatabase): PaymentMethodDao {
        return database.paymentMethodDao
    }
}
