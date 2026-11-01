package com.doonapos.app.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Double): String {
        return try {
            rupiahFormat.format(amount).replace("Rp", "Rp ")
        } catch (e: Exception) {
            "Rp ${amount.toLong()}"
        }
    }

    fun formatNumber(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", amount)
        }
    }
}
