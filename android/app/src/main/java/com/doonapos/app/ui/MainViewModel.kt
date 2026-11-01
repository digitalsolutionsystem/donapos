package com.doonapos.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.doonapos.app.data.local.*
import com.doonapos.app.data.model.CartItem
import com.doonapos.app.data.model.UserSession
import com.doonapos.app.data.repository.PosRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val customerName: String = "",
    val paymentMethod: String = "Tunai",
    val cashGiven: Double = 0.0,
    val isPaymentDialogOpen: Boolean = false,
    val completedTransaction: TransactionEntity? = null
) {
    val totalAmount: Double get() = items.sumOf { it.subtotal }
    val totalItemsCount: Int get() = items.sumOf { it.qty }
    val cashChange: Double get() = if (cashGiven >= totalAmount) cashGiven - totalAmount else 0.0
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val database = AppDatabase.getInstance(application)
    val userPreferences = UserPreferences(application)
    val repository = PosRepository(database, userPreferences)

    // Auth & Session
    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    // Data streams
    val stockItems = repository.stockItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val products = repository.products.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val consignmentProducts = repository.consignmentProducts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val consignmentTransactions = repository.consignmentTransactions.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val expenses = repository.expenses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val consignmentExpenses = repository.consignmentExpenses.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val partnerSetting = repository.partnerSetting.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // POS Cart State
    private val _cartState = MutableStateFlow(CartUiState())
    val cartState: StateFlow<CartUiState> = _cartState.asStateFlow()

    // Sync status toast/message
    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        // Auto-login to Kasir by default for immediate convenience or check session
        viewModelScope.launch {
            userPreferences.userRole.collect { role ->
                if (role != null) {
                    val name = userPreferences.userName.first()
                    val token = userPreferences.authToken.first()
                    _currentUser.value = UserSession(role, name, token)
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    // AUTH
    fun loginWithPin(role: String, pin: String) {
        viewModelScope.launch {
            val validPin = if (role == "owner") {
                userPreferences.ownerPin.first()
            } else {
                userPreferences.kasirPin.first()
            }

            if (pin == validPin) {
                val name = if (role == "owner") "Pemilik Toko" else "Kasir 1"
                userPreferences.saveSession(role, name, null)
                _currentUser.value = UserSession(role, name, null)
                _pinError.value = null
            } else {
                _pinError.value = "PIN salah untuk peran $role"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
            _currentUser.value = null
        }
    }

    fun clearPinError() {
        _pinError.value = null
    }

    // CART
    fun addToCart(product: ProductEntity) {
        val currentItems = _cartState.value.items.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.id == product.id && !it.isConsignment }
        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            currentItems[existingIndex] = item.copy(qty = item.qty + 1)
        } else {
            currentItems.add(
                CartItem(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    qty = 1,
                    isConsignment = false,
                    image = product.image,
                    ingredients = product.ingredients
                )
            )
        }
        _cartState.update { it.copy(items = currentItems) }
    }

    fun addConsignmentToCart(product: ConsignmentProductEntity) {
        val currentItems = _cartState.value.items.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.id == product.id && it.isConsignment }
        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            currentItems[existingIndex] = item.copy(qty = item.qty + 1)
        } else {
            currentItems.add(
                CartItem(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    qty = 1,
                    isConsignment = true,
                    vendor = product.vendor,
                    storePercent = product.storePercent,
                    costPrice = product.costPrice,
                    image = product.image
                )
            )
        }
        _cartState.update { it.copy(items = currentItems) }
    }

    fun updateCartQty(item: CartItem, delta: Int) {
        val currentItems = _cartState.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.id == item.id && it.isConsignment == item.isConsignment }
        if (index >= 0) {
            val newQty = currentItems[index].qty + delta
            if (newQty <= 0) {
                currentItems.removeAt(index)
            } else {
                currentItems[index] = currentItems[index].copy(qty = newQty)
            }
            _cartState.update { it.copy(items = currentItems) }
        }
    }

    fun clearCart() {
        _cartState.update {
            it.copy(
                items = emptyList(),
                customerName = "",
                cashGiven = 0.0,
                isPaymentDialogOpen = false,
                completedTransaction = null
            )
        }
    }

    fun setCustomerName(name: String) {
        _cartState.update { it.copy(customerName = name) }
    }

    fun setPaymentMethod(method: String) {
        _cartState.update { it.copy(paymentMethod = method) }
    }

    fun setCashGiven(amount: Double) {
        _cartState.update { it.copy(cashGiven = amount) }
    }

    fun openPaymentDialog() {
        _cartState.update {
            it.copy(
                isPaymentDialogOpen = true,
                cashGiven = it.totalAmount
            )
        }
    }

    fun closePaymentDialog() {
        _cartState.update { it.copy(isPaymentDialogOpen = false) }
    }

    fun dismissReceipt() {
        _cartState.update { it.copy(completedTransaction = null) }
    }

    fun processPayment() {
        viewModelScope.launch {
            val state = _cartState.value
            val cashier = _currentUser.value?.name ?: "Kasir 1"
            val result = repository.checkout(
                cartItems = state.items,
                customerName = state.customerName,
                cashierName = cashier,
                paymentMethod = state.paymentMethod,
                cashGiven = if (state.paymentMethod == "Tunai") state.cashGiven else state.totalAmount,
                cashChange = if (state.paymentMethod == "Tunai") state.cashChange else 0.0
            )

            result.onSuccess { trx ->
                _cartState.update {
                    it.copy(
                        items = emptyList(),
                        isPaymentDialogOpen = false,
                        completedTransaction = trx
                    )
                }
            }.onFailure { err ->
                _syncMessage.value = "Gagal memproses transaksi: ${err.message}"
            }
        }
    }

    // CLOUD SYNC
    fun syncWithCloud() {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = repository.syncWithCloud()
            _isSyncing.value = false
            res.onSuccess { msg ->
                _syncMessage.value = msg
            }.onFailure { err ->
                _syncMessage.value = "Sinkronisasi gagal: ${err.message}"
            }
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    // STOCK ACTIONS
    fun addStockQty(stockId: String, amount: Double) {
        viewModelScope.launch {
            repository.addStock(stockId, amount)
        }
    }

    fun saveStockItem(item: StockItemEntity) {
        viewModelScope.launch {
            repository.saveStockItem(item)
        }
    }

    fun deleteStockItem(item: StockItemEntity) {
        viewModelScope.launch {
            repository.deleteStockItem(item)
        }
    }

    // PRODUCT ACTIONS
    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // CONSIGNMENT ACTIONS
    fun saveConsignmentProduct(product: ConsignmentProductEntity) {
        viewModelScope.launch {
            repository.saveConsignmentProduct(product)
        }
    }

    fun deleteConsignmentProduct(product: ConsignmentProductEntity) {
        viewModelScope.launch {
            repository.deleteConsignmentProduct(product)
        }
    }

    // EXPENSES ACTIONS
    fun addExpense(category: String, amount: Double, note: String) {
        viewModelScope.launch {
            repository.addExpense(category, amount, note)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // SETTINGS ACTIONS
    fun updateApiUrl(url: String) {
        viewModelScope.launch {
            userPreferences.updateApiUrl(url)
        }
    }

    fun updatePin(role: String, newPin: String) {
        viewModelScope.launch {
            userPreferences.updatePin(role, newPin)
            _syncMessage.value = "PIN untuk $role berhasil diperbarui"
        }
    }
}
