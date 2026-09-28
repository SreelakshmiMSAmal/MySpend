package com.sreelakshmims.myspend.di

import com.sreelakshmims.myspend.data.repository.CategoryRepositoryImpl
import com.sreelakshmims.myspend.data.repository.ExpenseRepositoryImpl
import com.sreelakshmims.myspend.data.repository.PaymentMethodRepositoryImpl
import com.sreelakshmims.myspend.domain.repository.CategoryRepository
import com.sreelakshmims.myspend.domain.repository.ExpenseRepository
import com.sreelakshmims.myspend.domain.repository.PaymentMethodRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindPaymentMethodRepository(
        paymentMethodRepositoryImpl: PaymentMethodRepositoryImpl
    ): PaymentMethodRepository
}
