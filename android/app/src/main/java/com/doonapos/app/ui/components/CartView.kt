package com.doonapos.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doonapos.app.data.model.CartItem
import com.doonapos.app.ui.CartUiState
import com.doonapos.app.ui.theme.Ochre
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.ui.theme.SurfaceContainerHigh
import com.doonapos.app.util.Formatters

@Composable
fun CartView(
    cartState: CartUiState,
    onCustomerNameChange: (String) -> Unit,
    onUpdateQty: (CartItem, Int) -> Unit,
    onClearCart: () -> Unit,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = SurfaceCard,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pesanan Aktif",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${cartState.totalItemsCount} Item",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (cartState.items.isNotEmpty()) {
                    TextButton(
                        onClick = onClearCart,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reset", fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Customer Name Field
            OutlinedTextField(
                value = cartState.customerName,
                onValueChange = onCustomerNameChange,
                label = { Text("Nama Pelanggan", fontSize = 12.sp) },
                placeholder = { Text("Pelanggan Umum", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(8.dp))
            Divider(color = SurfaceContainerHigh)

            // Items List
            if (cartState.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Keranjang Kosong",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Pilih item dari menu di samping",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartState.items, key = { "${it.id}_${it.isConsignment}" }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrement = { onUpdateQty(item, 1) },
                            onDecrement = { onUpdateQty(item, -1) }
                        )
                    }
                }
            }

            Divider(color = SurfaceContainerHigh)
            Spacer(Modifier.height(8.dp))

            // Total Summary & Pay Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = Formatters.formatRupiah(cartState.totalAmount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineGreen
                )
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onPayClick,
                enabled = cartState.items.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
            ) {
                Text(
                    text = "Bayar Sekarang (${Formatters.formatRupiah(cartState.totalAmount)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SurfaceContainerHigh.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    if (item.isConsignment) {
                        Spacer(Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Ochre,
                            modifier = Modifier.padding(1.dp)
                        ) {
                            Text(
                                text = "Titip",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = Formatters.formatRupiah(item.price),
                    fontSize = 12.sp,
                    color = PineGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quantity Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "${item.qty}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
