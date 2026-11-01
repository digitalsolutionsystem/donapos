package com.doonapos.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doonapos.app.data.local.TransactionEntity
import com.doonapos.app.ui.components.ReceiptDialog
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.PineGreenContainer
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.util.Formatters

@Composable
fun HistoryScreen(
    transactions: List<TransactionEntity>
) {
    var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PineGreen)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Riwayat Transaksi Penjualan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Daftar struk belanja yang telah selesai diproses di kasir",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(14.dp))

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada riwayat transaksi", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(transactions, key = { it.id }) { trx ->
                    TransactionItemCard(
                        transaction = trx,
                        onClick = { selectedTransaction = trx }
                    )
                }
            }
        }
    }

    if (selectedTransaction != null) {
        ReceiptDialog(
            transaction = selectedTransaction!!,
            onDismiss = { selectedTransaction = null }
        )
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.id,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${transaction.date} • Kasir: ${transaction.cashier}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Pelanggan: ${transaction.customer} • ${transaction.items.size} jenis produk",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatters.formatRupiah(transaction.total),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineGreen
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = PineGreenContainer
                ) {
                    Text(
                        text = transaction.method,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PineGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
