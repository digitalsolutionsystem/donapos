package com.doonapos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Handshake
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
import com.doonapos.app.data.local.ConsignmentProductEntity
import com.doonapos.app.ui.theme.Ochre
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.ui.theme.SurfaceContainerHigh
import com.doonapos.app.util.Formatters

@Composable
fun ConsignmentScreen(
    consignmentProducts: List<ConsignmentProductEntity>,
    onSaveProduct: (ConsignmentProductEntity) -> Unit,
    onDeleteProduct: (ConsignmentProductEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF8A5A00),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk Titip Jual")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Handshake, contentDescription = null, tint = Color(0xFF8A5A00))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Titip Jual (Konsinyasi)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Kelola produk titipan dari mitra vendor dan pembagian hasil penjualan",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            if (consignmentProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada produk titip jual", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(consignmentProducts, key = { it.id }) { product ->
                        ConsignmentItemCard(
                            product = product,
                            onDelete = { onDeleteProduct(product) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddConsignmentDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newProd ->
                onSaveProduct(newProd)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ConsignmentItemCard(
    product: ConsignmentProductEntity,
    onDelete: () -> Unit
) {
    val vendorPayoutPerItem = product.price * (100 - product.storePercent) / 100.0
    val storeProfitPerItem = product.price * product.storePercent / 100.0
    val totalVendorPayable = vendorPayoutPerItem * product.soldQty

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
                            text = product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Ochre
                        ) {
                            Text(
                                text = product.vendor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Harga: ${Formatters.formatRupiah(product.price)} | Bagi Toko: ${product.storePercent.toInt()}%",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SurfaceContainerHigh.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Sisa Stok", fontSize = 10.sp, color = Color.Gray)
                        Text("${product.qty.toInt()} Pcs", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text("Terjual", fontSize = 10.sp, color = Color.Gray)
                        Text("${product.soldQty.toInt()} Pcs", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PineGreen)
                    }
                    Column {
                        Text("Hutang ke Mitra", fontSize = 10.sp, color = Color.Gray)
                        Text(Formatters.formatRupiah(totalVendorPayable), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF8A5A00))
                    }
                }
            }
        }
    }
}

@Composable
fun AddConsignmentDialog(
    onDismiss: () -> Unit,
    onSave: (ConsignmentProductEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Pastry") }
    var priceStr by remember { mutableStateOf("") }
    var storePercentStr by remember { mutableStateOf("20") }
    var qtyStr by remember { mutableStateOf("10") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tambah Produk Titip Jual", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = vendor,
                    onValueChange = { vendor = it },
                    label = { Text("Nama Mitra / Vendor (Contoh: Dapur Bunda)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Harga Jual ke Pelanggan (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = storePercentStr,
                        onValueChange = { storePercentStr = it },
                        label = { Text("Komisi Toko (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = qtyStr,
                        onValueChange = { qtyStr = it },
                        label = { Text("Jumlah Stok") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

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
                            val id = "csg-${System.currentTimeMillis().toString().takeLast(6)}"
                            val price = priceStr.toDoubleOrNull() ?: 0.0
                            val pct = storePercentStr.toDoubleOrNull() ?: 20.0
                            val q = qtyStr.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && vendor.isNotBlank()) {
                                onSave(
                                    ConsignmentProductEntity(
                                        id = id,
                                        name = name,
                                        vendor = vendor,
                                        category = category,
                                        price = price,
                                        storePercent = pct,
                                        costPrice = price * (100 - pct) / 100.0,
                                        qty = q,
                                        soldQty = 0.0
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                    ) {
                        Text("Simpan Titip Jual")
                    }
                }
            }
        }
    }
}
