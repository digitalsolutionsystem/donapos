package com.doonapos.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.PineGreenContainer
import com.doonapos.app.ui.theme.SurfaceCard

@Composable
fun PinLoginScreen(
    errorMessage: String?,
    onLogin: (role: String, pin: String) -> Unit,
    onClearError: () -> Unit
) {
    var selectedRole by remember { mutableStateOf("kasir") }
    var pinInput by remember { mutableStateOf("") }

    LaunchedEffect(pinInput) {
        if (pinInput.length >= 4) {
            onLogin(selectedRole, pinInput)
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            pinInput = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD5DDD3)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceCard,
            shadowElevation = 8.dp,
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = PineGreenContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.PointOfSale,
                            contentDescription = null,
                            tint = PineGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "DoonaPOS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineGreen
                )
                Text(
                    text = "Masukkan PIN untuk melanjutkan",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                // Role Toggle (Kasir vs Pemilik)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8EDE5))
                        .padding(4.dp)
                ) {
                    val isKasir = selectedRole == "kasir"
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isKasir) PineGreen else Color.Transparent)
                            .clickable {
                                selectedRole = "kasir"
                                pinInput = ""
                                onClearError()
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Kasir (1234)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isKasir) Color.White else Color.Black
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isKasir) PineGreen else Color.Transparent)
                            .clickable {
                                selectedRole = "owner"
                                pinInput = ""
                                onClearError()
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Pemilik (8888)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!isKasir) Color.White else Color.Black
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // PIN Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isFilled = index < pinInput.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) PineGreen else Color(0xFFD0D7CF))
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Numeric Keypad
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "DEL")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in keys) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (key in row) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (key == "C" || key == "DEL") Color(0xFFE8EDE5) else Color(0xFFF4F6F3),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clickable {
                                            onClearError()
                                            when (key) {
                                                "C" -> pinInput = ""
                                                "DEL" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                                else -> if (pinInput.length < 8) pinInput += key
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (key == "DEL") {
                                            Icon(
                                                Icons.Default.Backspace,
                                                contentDescription = "Hapus",
                                                tint = Color.DarkGray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            Text(
                                                text = key,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
