package com.doonapos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
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
import com.doonapos.app.data.local.ProductEntity
import com.doonapos.app.data.local.StockItemEntity
import com.doonapos.app.data.model.Ingredient
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.ui.theme.SurfaceContainerHigh
import com.doonapos.app.util.Formatters

@Composable
fun ProductScreen(
    products: List<ProductEntity>,
    stockItems: List<StockItemEntity>,
    onSaveProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PineGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Menu / Produk")
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
                text = "Manajemen Produk & Resep",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Daftar produk yang dijual beserta racikan resep bahan bakunya",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada produk terdaftar", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            stockItems = stockItems,
                            onDelete = { onDeleteProduct(product) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            stockItems = stockItems,
            onDismiss = { showAddDialog = false },
            onSave = { newProd ->
                onSaveProduct(newProd)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ProductItemCard(
    product: ProductEntity,
    stockItems: List<StockItemEntity>,
    onDelete: () -> Unit
) {
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
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Kategori: ${product.category}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = Formatters.formatRupiah(product.price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PineGreen
                )
            }

            Spacer(Modifier.height(8.dp))

            // Ingredients / BOM summary
            if (product.ingredients.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceContainerHigh.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp), tint = PineGreen)
                            Spacer(Modifier.width(4.dp))
                            Text("Resep (Bahan Mentah):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(4.dp))
                        for (ing in product.ingredients) {
                            val stock = stockItems.firstOrNull { it.id == ing.stockId }
                            val stockName = stock?.name ?: ing.stockId
                            val unit = stock?.unit ?: ""
                            Text(
                                text = "• $stockName: ${Formatters.formatNumber(ing.amount)} $unit",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    stockItems: List<StockItemEntity>,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Kopi") }
    var priceStr by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val ingredientsList = remember { mutableStateListOf<Ingredient>() }
    var selectedStockId by remember { mutableStateOf(stockItems.firstOrNull()?.id ?: "") }
    var amountStr by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tambah Produk Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (Kopi, Non-Kopi, Makanan)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Harga Jual (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))
                Text("Resep Bahan Baku:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

                // Add Ingredient sub-row
                if (stockItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedTextField(
                            value = amountStr,
                            onValueChange = { amountStr = it },
                            label = { Text("Jumlah") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val amt = amountStr.toDoubleOrNull() ?: 0.0
                                if (amt > 0 && selectedStockId.isNotBlank()) {
                                    ingredientsList.add(Ingredient(selectedStockId, amt))
                                    amountStr = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                        ) {
                            Text("+ Bahan")
                        }
                    }

                    // Display current added ingredients
                    for (ing in ingredientsList) {
                        val stName = stockItems.firstOrNull { it.id == ing.stockId }?.name ?: ing.stockId
                        Text("• $stName (${ing.amount})", fontSize = 11.sp)
                    }
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
                            val id = "prd-${System.currentTimeMillis().toString().takeLast(6)}"
                            val price = priceStr.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank()) {
                                onSave(
                                    ProductEntity(
                                        id = id,
                                        name = name,
                                        category = category,
                                        price = price,
                                        image = imageUrl,
                                        ingredients = ingredientsList.toList()
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                    ) {
                        Text("Simpan Produk")
                    }
                }
            }
        }
    }
}
