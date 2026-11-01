package com.doonapos.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.doonapos.app.ui.CartUiState
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.PineGreenContainer
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.ui.theme.SurfaceContainerHigh
import com.doonapos.app.util.Formatters

@Composable
fun PaymentDialog(
    cartState: CartUiState,
    onPaymentMethodSelect: (String) -> Unit,
    onCashGivenChange: (Double) -> Unit,
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    val methods = listOf("Tunai", "QRIS", "Debit", "Transfer")
    val quickNominals = listOf(
        cartState.totalAmount,
        10000.0,
        20000.0,
        50000.0,
        100000.0
    ).distinct().sorted()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Pembayaran",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                // Total Amount Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PineGreenContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL TAGIHAN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PineGreen
                        )
                        Text(
                            text = Formatters.formatRupiah(cartState.totalAmount),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PineGreen
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Payment Method Selector
                Text(
                    text = "Metode Pembayaran",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (method in methods) {
                        val isSelected = cartState.paymentMethod == method
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PineGreen else SurfaceContainerHigh,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onPaymentMethodSelect(method) }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = method,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }

                // If Cash, show quick nominal buttons & change
                if (cartState.paymentMethod == "Tunai") {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Uang Diterima",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickNominals) { nominal ->
                            val isExact = nominal == cartState.totalAmount
                            AssistChip(
                                onClick = { onCashGivenChange(nominal) },
                                label = {
                                    Text(
                                        text = if (isExact) "Uang Pas" else Formatters.formatRupiah(nominal),
                                        fontSize = 11.sp
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (cartState.cashGiven == nominal) PineGreenContainer else SurfaceContainerHigh
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = if (cartState.cashGiven > 0) cartState.cashGiven.toLong().toString() else "",
                        onValueChange = { str ->
                            val num = str.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
                            onCashGivenChange(num)
                        },
                        label = { Text("Jumlah Tunai Diterima (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    // Kembalian
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kembalian:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = Formatters.formatRupiah(cartState.cashChange),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cartState.cashGiven >= cartState.totalAmount) Color(0xFF1E6B3A) else Color.Red
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Batal")
                    }

                    val canConfirm = cartState.paymentMethod != "Tunai" || cartState.cashGiven >= cartState.totalAmount

                    Button(
                        onClick = onConfirmPayment,
                        enabled = canConfirm,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                    ) {
                        Text("Selesaikan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
