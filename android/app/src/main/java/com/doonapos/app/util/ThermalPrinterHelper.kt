package com.doonapos.app.util

import android.content.Context
import android.content.Intent
import com.doonapos.app.data.local.TransactionEntity

object ThermalPrinterHelper {

    fun generateReceiptText(transaction: TransactionEntity, storeName: String = "DOONAPOS"): String {
        val width = 32
        val sb = StringBuilder()

        fun center(text: String): String {
            val pad = (width - text.length) / 2
            return if (pad > 0) " ".repeat(pad) + text else text
        }

        fun row(left: String, right: String): String {
            val space = width - left.length - right.length
            return if (space > 0) left + " ".repeat(space) + right else "$left $right"
        }

        val divider = "-".repeat(width)
        val doubleDivider = "=".repeat(width)

        sb.appendLine(center(storeName))
        sb.appendLine(center("Sistem Kasir & Inventaris"))
        sb.appendLine(divider)
        sb.appendLine(row("No. Struk:", transaction.id))
        sb.appendLine(row("Waktu:", transaction.date))
        sb.appendLine(row("Kasir:", transaction.cashier))
        sb.appendLine(row("Pelanggan:", transaction.customer))
        sb.appendLine(doubleDivider)

        for (item in transaction.items) {
            sb.appendLine(item.name)
            val qtyPrice = "${item.qty} x ${Formatters.formatRupiah(item.price)}"
            val subtotal = Formatters.formatRupiah(item.subtotal)
            sb.appendLine(row(qtyPrice, subtotal))
        }

        sb.appendLine(divider)
        sb.appendLine(row("TOTAL:", Formatters.formatRupiah(transaction.total)))
        sb.appendLine(row("Metode:", transaction.method))
        if (transaction.method.equals("Tunai", ignoreCase = true)) {
            sb.appendLine(row("Bayar Tunai:", Formatters.formatRupiah(transaction.cashGiven)))
            sb.appendLine(row("Kembalian:", Formatters.formatRupiah(transaction.cashChange)))
        }
        sb.appendLine(divider)
        sb.appendLine(center("Terima Kasih"))
        sb.appendLine(center("Semoga Harimu Menyenangkan!"))
        sb.appendLine("\n\n")

        return sb.toString()
    }

    fun shareReceipt(context: Context, transaction: TransactionEntity) {
        val text = generateReceiptText(transaction)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Struk Pembelian - ${transaction.id}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan / Cetak Struk"))
    }
}
