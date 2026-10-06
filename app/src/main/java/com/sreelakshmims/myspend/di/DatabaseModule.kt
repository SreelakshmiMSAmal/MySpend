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
import com.sreelakshmims.myspend.domain.model.NecessityLevel
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
        )
            .addMigrations(MySpendDatabase.MIGRATION_1_2, MySpendDatabase.MIGRATION_2_3)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateCategories(categoryDaoProvider.get())
                        prepopulatePaymentMethods(paymentMethodDaoProvider.get())
                    }
                }
            })
            .build()
    }

    private suspend fun prepopulateCategories(dao: CategoryDao) {
        val categories = getInitialCategories()
        categories.forEach { dao.insertCategory(it) }
    }

    fun getInitialCategories(): List<CategoryEntity> {
        return listOf(
            // NECESSITY
            CategoryEntity(name = "EMI", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Fuel & Vehicle", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Parents", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Insurance", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Food", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Groceries", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Transport", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Rent", necessity = NecessityLevel.NECESSITY, isDefault = true),
            CategoryEntity(name = "Bills", necessity = NecessityLevel.NECESSITY, isDefault = true),

            // SEMI_NECESSITY
            CategoryEntity(name = "Home Maintenance", necessity = NecessityLevel.SEMI_NECESSITY, isDefault = true),
            CategoryEntity(name = "Professional", necessity = NecessityLevel.SEMI_NECESSITY, isDefault = true),
            CategoryEntity(name = "Health", necessity = NecessityLevel.SEMI_NECESSITY, isDefault = true),
            CategoryEntity(name = "Education", necessity = NecessityLevel.SEMI_NECESSITY, isDefault = true),

            // SAVINGS
            CategoryEntity(name = "Savings", necessity = NecessityLevel.SAVINGS, isDefault = true),

            // NOT_NECESSARY
            CategoryEntity(name = "Gifts & Celebrations", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true),
            CategoryEntity(name = "Charity", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true),
            CategoryEntity(name = "Shopping", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true),
            CategoryEntity(name = "Entertainment", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true),
            CategoryEntity(name = "Subscriptions", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true),
            CategoryEntity(name = "Travel", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true),
            CategoryEntity(name = "Personal", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true),
            CategoryEntity(name = "Others", necessity = NecessityLevel.NOT_NECESSARY, isDefault = true)
        )
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
