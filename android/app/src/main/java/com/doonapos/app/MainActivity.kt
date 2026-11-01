package com.doonapos.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doonapos.app.ui.MainViewModel
import com.doonapos.app.ui.components.DoonaTopBar
import com.doonapos.app.ui.components.PaymentDialog
import com.doonapos.app.ui.components.ReceiptDialog
import com.doonapos.app.ui.screens.*
import com.doonapos.app.ui.theme.DoonaPOSTheme
import com.doonapos.app.ui.theme.PineGreen
import com.doonapos.app.ui.theme.PineGreenContainer
import kotlinx.coroutines.launch

enum class ScreenTab(val label: String, val icon: ImageVector) {
    POS("Kasir (POS)", Icons.Default.PointOfSale),
    PRODUCTS("Produk & Resep", Icons.Default.Inventory),
    STOCK("Bahan Baku & Stok", Icons.Default.Warehouse),
    CONSIGNMENT("Titip Jual", Icons.Default.Handshake),
    HISTORY("Riwayat Struk", Icons.Default.ReceiptLong),
    REPORT("Laporan Keuangan", Icons.Default.Assessment),
    EXPENSES("Beban Toko", Icons.Default.MoneyOff),
    SETTINGS("Pengaturan", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoonaPOSTheme {
                MainAppScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(ScreenTab.POS) }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val pinError by viewModel.pinError.collectAsStateWithLifecycle()
    val cartState by viewModel.cartState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val consignmentProducts by viewModel.consignmentProducts.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val currentApiUrl by viewModel.userPreferences.apiUrl.collectAsStateWithLifecycle(initialValue = "")

    LaunchedEffect(syncMessage) {
        if (syncMessage != null) {
            Toast.makeText(context, syncMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearSyncMessage()
        }
    }

    // If no active user session, show PIN Login Screen
    if (currentUser == null) {
        PinLoginScreen(
            errorMessage = pinError,
            onLogin = { role, pin -> viewModel.loginWithPin(role, pin) },
            onClearError = { viewModel.clearPinError() }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFFBFCFA),
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header Brand
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PineGreenContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PointOfSale, contentDescription = null, tint = PineGreen)
                            }
                        }
                        Column {
                            Text("DoonaPOS", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PineGreen)
                            Text("Sistem Kasir & Stok", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Divider(color = Color(0xFFE7EDE5))
                    Spacer(Modifier.height(10.dp))

                    // Menu Items
                    ScreenTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PineGreenContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable {
                                    currentTab = tab
                                    scope.launch { drawerState.close() }
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    tab.icon,
                                    contentDescription = tab.label,
                                    tint = if (isSelected) PineGreen else Color.DarkGray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = tab.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PineGreen else Color.Black
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Footer App Info
                    Divider(color = Color(0xFFE7EDE5))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "DoonaPOS Native v2.4 (Kotlin)",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                DoonaTopBar(
                    currentUser = currentUser,
                    isSyncing = isSyncing,
                    onSyncClick = { viewModel.syncWithCloud() },
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onLogoutClick = { viewModel.logout() }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFD5DDD3))
            ) {
                when (currentTab) {
                    ScreenTab.POS -> PosScreen(
                        products = products,
                        consignmentProducts = consignmentProducts,
                        cartState = cartState,
                        onAddToCart = { viewModel.addToCart(it) },
                        onAddConsignmentToCart = { viewModel.addConsignmentToCart(it) },
                        onCustomerNameChange = { viewModel.setCustomerName(it) },
                        onUpdateQty = { item, delta -> viewModel.updateCartQty(item, delta) },
                        onClearCart = { viewModel.clearCart() },
                        onPayClick = { viewModel.openPaymentDialog() }
                    )

                    ScreenTab.PRODUCTS -> ProductScreen(
                        products = products,
                        stockItems = stockItems,
                        onSaveProduct = { viewModel.saveProduct(it) },
                        onDeleteProduct = { viewModel.deleteProduct(it) }
                    )

                    ScreenTab.STOCK -> StockScreen(
                        stockItems = stockItems,
                        onAddStock = { id, amount -> viewModel.addStockQty(id, amount) },
                        onSaveStock = { viewModel.saveStockItem(it) },
                        onDeleteStock = { viewModel.deleteStockItem(it) }
                    )

                    ScreenTab.CONSIGNMENT -> ConsignmentScreen(
                        consignmentProducts = consignmentProducts,
                        onSaveProduct = { viewModel.saveConsignmentProduct(it) },
                        onDeleteProduct = { viewModel.deleteConsignmentProduct(it) }
                    )

                    ScreenTab.HISTORY -> HistoryScreen(
                        transactions = transactions
                    )

                    ScreenTab.REPORT -> ReportScreen(
                        transactions = transactions,
                        expenses = expenses
                    )

                    ScreenTab.EXPENSES -> ExpenseScreen(
                        expenses = expenses,
                        onAddExpense = { cat, amt, note -> viewModel.addExpense(cat, amt, note) },
                        onDeleteExpense = { viewModel.deleteExpense(it) }
                    )

                    ScreenTab.SETTINGS -> SettingsScreen(
                        currentApiUrl = currentApiUrl,
                        isSyncing = isSyncing,
                        onUpdateApiUrl = { viewModel.updateApiUrl(it) },
                        onSyncNow = { viewModel.syncWithCloud() },
                        onUpdatePin = { role, pin -> viewModel.updatePin(role, pin) }
                    )
                }
            }

            // Payment Modal Dialog
            if (cartState.isPaymentDialogOpen) {
                PaymentDialog(
                    cartState = cartState,
                    onPaymentMethodSelect = { viewModel.setPaymentMethod(it) },
                    onCashGivenChange = { viewModel.setCashGiven(it) },
                    onConfirmPayment = { viewModel.processPayment() },
                    onDismiss = { viewModel.closePaymentDialog() }
                )
            }

            // Completed Receipt Modal Dialog
            if (cartState.completedTransaction != null) {
                ReceiptDialog(
                    transaction = cartState.completedTransaction!!,
                    onDismiss = { viewModel.dismissReceipt() }
                )
            }
        }
    }
}
