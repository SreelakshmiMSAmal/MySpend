package com.sreelakshmims.myspend.domain.model

data class PaymentMethod(
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false
)
