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
import com.sreelakshmims.myspend.data.local.entity.ExpenseEntity
import com.sreelakshmims.myspend.data.local.entity.PaymentMethodEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
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
        paymentMethodDaoProvider: Provider<PaymentMethodDao>,
        expenseProvider: Provider<ExpenseDao>
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
                    getInitialExpenses(expenseProvider.get())
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

    private suspend fun getInitialExpenses(dao: ExpenseDao){
        fun getTimestamp(monthOffset: Int, dayOfMonth: Int, hour: Int = 12): Long {
            return Calendar.getInstance().apply {
                add(Calendar.MONTH, monthOffset)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        val lists : List<ExpenseEntity>  = listOf(
            // ================= CURRENT MONTH (10 items) 12319=================
            ExpenseEntity(amountPaise = 25000, categoryId = 1, paymentMethodId = 1, expenseDate = getTimestamp(0, 1), merchant = "Starbucks", note = "Morning Coffee"),
            ExpenseEntity(amountPaise = 142000, categoryId = 2, paymentMethodId = 2, expenseDate = getTimestamp(0, 2), merchant = "DMart", note = "Weekly Grocery"),
            ExpenseEntity(amountPaise = 35000, categoryId = 3, paymentMethodId = 1, expenseDate = getTimestamp(0, 3), merchant = "Uber", note = "Ride to Office"),
            ExpenseEntity(amountPaise = 89900, categoryId = 4, paymentMethodId = 2, expenseDate = getTimestamp(0, 4), merchant = "Netflix", note = "Monthly Subscription"),
            ExpenseEntity(amountPaise = 45000, categoryId = 1, paymentMethodId = 3, expenseDate = getTimestamp(0, 5), merchant = "Zomato", note = "Dinner with friends"),
            ExpenseEntity(amountPaise = 210000, categoryId = 5, paymentMethodId = 2, expenseDate = getTimestamp(0, 6), merchant = "HP Petrol Pump", note = "Fuel Refill"),
            ExpenseEntity(amountPaise = 65000, categoryId = 6, paymentMethodId = 1, expenseDate = getTimestamp(0, 7), merchant = "Apollo Pharmacy", note = "Medicines"),
            ExpenseEntity(amountPaise = 180000, categoryId = 2, paymentMethodId = 3, expenseDate = getTimestamp(0, 8), merchant = "BigBasket", note = "Household Essentials"),
            ExpenseEntity(amountPaise = 320000, categoryId = 7, paymentMethodId = 2, expenseDate = getTimestamp(0, 9), merchant = "Zara", note = "New Shirt"),
            ExpenseEntity(amountPaise = 120000, categoryId = 1, paymentMethodId = 1, expenseDate = getTimestamp(0, 10), merchant = "Swiggy", note = "Weekend Lunch"),

            // ================= PREVIOUS MONTH (10 items)19399 =================
            ExpenseEntity(amountPaise = 150000, categoryId = 2, paymentMethodId = 2, expenseDate = getTimestamp(-1, 2), merchant = "Reliance Fresh", note = "Monthly Supplies"),
            ExpenseEntity(amountPaise = 40000, categoryId = 3, paymentMethodId = 1, expenseDate = getTimestamp(-1, 5), merchant = "Ola", note = "Cab to Station"),
            ExpenseEntity(amountPaise = 29900, categoryId = 4, paymentMethodId = 2, expenseDate = getTimestamp(-1, 8), merchant = "Spotify", note = "Music Subscription"),
            ExpenseEntity(amountPaise = 550000, categoryId = 8, paymentMethodId = 2, expenseDate = getTimestamp(-1, 11), merchant = "Electricity Board", note = "Electricity Bill"),
            ExpenseEntity(amountPaise = 85000, categoryId = 1, paymentMethodId = 3, expenseDate = getTimestamp(-1, 14), merchant = "Dominos", note = "Pizza Night"),
            ExpenseEntity(amountPaise = 120000, categoryId = 6, paymentMethodId = 1, expenseDate = getTimestamp(-1, 18), merchant = "Cult.fit", note = "Gym Pass"),
            ExpenseEntity(amountPaise = 230000, categoryId = 5, paymentMethodId = 2, expenseDate = getTimestamp(-1, 21), merchant = "Shell Petrol Pump", note = "Full Tank Fuel"),
            ExpenseEntity(amountPaise = 110000, categoryId = 2, paymentMethodId = 1, expenseDate = getTimestamp(-1, 24), merchant = "Local Market", note = "Fruits & Veggies"),
            ExpenseEntity(amountPaise = 450000, categoryId = 7, paymentMethodId = 2, expenseDate = getTimestamp(-1, 27), merchant = "Amazon", note = "Electronics Accessary"),
            ExpenseEntity(amountPaise = 175000, categoryId = 1, paymentMethodId = 3, expenseDate = getTimestamp(-1, 28), merchant = "Barbeque Nation", note = "Family Dinner")
        )
        lists.forEach {
            dao.insertExpense(it)
        }
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
