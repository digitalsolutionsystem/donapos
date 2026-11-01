package com.doonapos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doonapos.app.data.local.ExpenseEntity
import com.doonapos.app.data.local.TransactionEntity
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.PineGreenContainer
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.ui.theme.SurfaceContainerHigh
import com.doonapos.app.util.Formatters

@Composable
fun ReportScreen(
    transactions: List<TransactionEntity>,
    expenses: List<ExpenseEntity>
) {
    val totalRevenue = remember(transactions) {
        transactions.sumOf { it.total }
    }

    val totalExpenses = remember(expenses) {
        expenses.sumOf { it.amount }
    }

    val netProfit = totalRevenue - totalExpenses

    val totalItemsSold = remember(transactions) {
        transactions.flatMap { it.items }.sumOf { it.qty }
    }

    // Top selling products
    val topSelling = remember(transactions) {
        val map = mutableMapOf<String, Int>()
        for (trx in transactions) {
            for (item in trx.items) {
                map[item.name] = (map[item.name] ?: 0) + item.qty
            }
        }
        map.toList().sortedByDescending { it.second }.take(5)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Assessment, contentDescription = null, tint = PineGreen)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Laporan Penjualan & Keuangan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Ringkasan performa omset, pengeluaran, dan produk terlaris",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        // Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total Omset",
                value = Formatters.formatRupiah(totalRevenue),
                icon = Icons.Default.TrendingUp,
                color = PineGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Laba Bersih",
                value = Formatters.formatRupiah(netProfit),
                icon = Icons.Default.AttachMoney,
                color = if (netProfit >= 0) PineGreen else Color.Red,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Pengeluaran",
                value = Formatters.formatRupiah(totalExpenses),
                icon = Icons.Default.AttachMoney,
                color = Color(0xFFBA1A1A),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Item Terjual",
                value = "$totalItemsSold Pcs",
                icon = Icons.Default.ShoppingCart,
                color = Color(0xFF1F5A8A),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Top Selling Products Section
        Text(
            text = "5 Produk Terlaris (Best Sellers)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        if (topSelling.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("Belum ada data penjualan", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    topSelling.forEachIndexed { index, pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = PineGreenContainer,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PineGreen)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(pair.first, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }

                            Text("${pair.second} terjual", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PineGreen)
                        }
                        if (index < topSelling.lastIndex) {
                            Divider(color = SurfaceContainerHigh)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = Color.Gray)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
