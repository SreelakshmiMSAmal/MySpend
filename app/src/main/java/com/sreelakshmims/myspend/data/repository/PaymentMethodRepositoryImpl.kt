package com.sreelakshmims.myspend.data.repository

import com.sreelakshmims.myspend.data.local.dao.PaymentMethodDao
import com.sreelakshmims.myspend.data.toEntity
import com.sreelakshmims.myspend.data.toPaymentMethod
import com.sreelakshmims.myspend.domain.model.PaymentMethod
import com.sreelakshmims.myspend.domain.repository.PaymentMethodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PaymentMethodRepositoryImpl @Inject constructor(
    private val paymentMethodDao: PaymentMethodDao
) : PaymentMethodRepository {

    override suspend fun insertPaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.insertPaymentMethod(paymentMethod.toEntity())
    }

    override suspend fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.updatePaymentMethod(paymentMethod.toEntity())
    }

    override suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.deletePaymentMethod(paymentMethod.toEntity())
    }

    override fun getAllPaymentMethods(): Flow<List<PaymentMethod>> {
        return paymentMethodDao.getAllPaymentMethods().map { entities ->
            entities.map { it.toPaymentMethod() }
        }
    }

    override suspend fun getPaymentMethodById(id: Long): PaymentMethod? {
        return paymentMethodDao.getPaymentMethodById(id)?.toPaymentMethod()
    }

    override suspend fun getPaymentMethodCount(): Int {
        return paymentMethodDao.getPaymentMethodCount()
    }
}
