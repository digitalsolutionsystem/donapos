package com.doonapos.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.doonapos.app.data.local.TransactionEntity
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.SurfaceCard
import com.doonapos.app.util.Formatters
import com.doonapos.app.util.ThermalPrinterHelper

@Composable
fun ReceiptDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PineGreen,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Transaksi Berhasil!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineGreen
                )

                Spacer(Modifier.height(14.dp))

                // Simulated Thermal Receipt Paper
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF7F8F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = ThermalPrinterHelper.generateReceiptText(transaction),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFF222222)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Print / Share Button
                Button(
                    onClick = {
                        ThermalPrinterHelper.shareReceipt(context, transaction)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cetak / Bagikan Struk", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Transaksi Baru")
                }
            }
        }
    }
}
