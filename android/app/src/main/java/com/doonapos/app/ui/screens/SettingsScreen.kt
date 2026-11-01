package com.doonapos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.SurfaceCard

@Composable
fun SettingsScreen(
    currentApiUrl: String,
    isSyncing: Boolean,
    onUpdateApiUrl: (String) -> Unit,
    onSyncNow: () -> Unit,
    onUpdatePin: (role: String, newPin: String) -> Unit
) {
    var apiUrlInput by remember(currentApiUrl) { mutableStateOf(currentApiUrl) }
    var kasirPinInput by remember { mutableStateOf("") }
    var ownerPinInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = PineGreen)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Pengaturan Aplikasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Konfigurasi server Google Sheets API, sinkronisasi cloud, dan keamanan PIN",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        // Google Sheets Sync Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = PineGreen)
                    Spacer(Modifier.width(8.dp))
                    Text("Integrasi Google Sheets (GAS)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "URL Web App Google Apps Script tempat database spreadsheet terhubung.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = apiUrlInput,
                    onValueChange = { apiUrlInput = it },
                    label = { Text("URL Web App GAS (/exec)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onUpdateApiUrl(apiUrlInput) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simpan URL")
                    }

                    Button(
                        onClick = {
                            onUpdateApiUrl(apiUrlInput)
                            onSyncNow()
                        },
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Sinkronkan Sekarang")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Security PIN Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PineGreen)
                    Spacer(Modifier.width(8.dp))
                    Text("Keamanan & Ganti PIN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(Modifier.height(10.dp))

                // Kasir PIN
                Text("Ganti PIN Kasir (Default: 1234)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = kasirPinInput,
                        onValueChange = { if (it.length <= 8) kasirPinInput = it },
                        label = { Text("PIN Baru Kasir") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (kasirPinInput.isNotBlank()) {
                                onUpdatePin("kasir", kasirPinInput)
                                kasirPinInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                    ) {
                        Text("Ubah")
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Owner PIN
                Text("Ganti PIN Pemilik (Default: 8888)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ownerPinInput,
                        onValueChange = { if (it.length <= 8) ownerPinInput = it },
                        label = { Text("PIN Baru Pemilik") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (ownerPinInput.isNotBlank()) {
                                onUpdatePin("owner", ownerPinInput)
                                ownerPinInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                    ) {
                        Text("Ubah")
                    }
                }
            }
        }
    }
}
