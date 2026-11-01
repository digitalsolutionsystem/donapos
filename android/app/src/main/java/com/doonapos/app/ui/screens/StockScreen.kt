package com.doonapos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.doonapos.app.data.local.StockItemEntity
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.PineGreenContainer
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.ui.theme.SurfaceContainerHigh
import com.doonapos.app.util.Formatters

@Composable
fun StockScreen(
    stockItems: List<StockItemEntity>,
    onAddStock: (stockId: String, amount: Double) -> Unit,
    onSaveStock: (StockItemEntity) -> Unit,
    onDeleteStock: (StockItemEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PineGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Bahan Baku")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Gudang Bahan Baku (Inventory)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Stok bahan baku otomatis berkurang berdasarkan resep saat transaksi kasir",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            if (stockItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada bahan baku", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(stockItems, key = { it.id }) { item ->
                        StockItemCard(
                            item = item,
                            onQuickAdd = { amount -> onAddStock(item.id, amount) },
                            onDelete = { onDeleteStock(item) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddStockItemDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                onSaveStock(newItem)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StockItemCard(
    item: StockItemEntity,
    onQuickAdd: (Double) -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = item.qty <= item.minLimit

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (isLowStock) {
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFDAD6)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("Kritis", color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Text(
                        text = "ID: ${item.id} | Min Limit: ${Formatters.formatNumber(item.minLimit)} ${item.unit}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${Formatters.formatNumber(item.qty)} ${item.unit}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLowStock) Color.Red else PineGreen
                )

                // Quick Add Chips (+10, +50, +100)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(10.0, 50.0, 100.0, 500.0).forEach { addVal ->
                        FilledTonalButton(
                            onClick = { onQuickAdd(addVal) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+${addVal.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddStockItemDialog(
    onDismiss: () -> Unit,
    onSave: (StockItemEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("gram") }
    var qty by remember { mutableStateOf("") }
    var minLimit by remember { mutableStateOf("") }

    val units = listOf("gram", "ml", "pcs", "lembar", "kg", "liter")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tambah Bahan Baku Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Bahan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Satuan (gram, ml, pcs)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Jumlah Awal (Qty)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = minLimit,
                    onValueChange = { minLimit = it },
                    label = { Text("Batas Peringatan Minimum (Min Limit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val id = "stk-${System.currentTimeMillis().toString().takeLast(6)}"
                            val q = qty.toDoubleOrNull() ?: 0.0
                            val m = minLimit.toDoubleOrNull() ?: 10.0
                            if (name.isNotBlank()) {
                                onSave(StockItemEntity(id, name, unit, q, m))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}
