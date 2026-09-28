package com.sreelakshmims.myspend.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formatPaiseToRupees(paise: Long): String {
        val rupees = paise / 100.0
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return format.format(rupees)
    }

    fun rupeesToPaise(rupees: Double): Long {
        return (rupees * 100).toLong()
    }
}
