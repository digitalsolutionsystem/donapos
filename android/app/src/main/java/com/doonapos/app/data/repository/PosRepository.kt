package com.doonapos.app.data.repository

import com.doonapos.app.data.local.*
import com.doonapos.app.data.model.CartItem
import com.doonapos.app.data.remote.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PosRepository(
    private val database: AppDatabase,
    private val userPreferences: UserPreferences
) {
    private val stockItemDao = database.stockItemDao()
    private val productDao = database.productDao()
    private val consignmentProductDao = database.consignmentProductDao()
    private val transactionDao = database.transactionDao()
    private val consignmentTransactionDao = database.consignmentTransactionDao()
    private val expenseDao = database.expenseDao()
    private val consignmentExpenseDao = database.consignmentExpenseDao()
    private val partnerSettingDao = database.partnerSettingDao()
    private val gson = Gson()

    // Observables
    val stockItems: Flow<List<StockItemEntity>> = stockItemDao.getAllStockItems()
    val products: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val consignmentProducts: Flow<List<ConsignmentProductEntity>> = consignmentProductDao.getAllConsignmentProducts()
    val transactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val consignmentTransactions: Flow<List<ConsignmentTransactionEntity>> = consignmentTransactionDao.getAllConsignmentTransactions()
    val expenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val consignmentExpenses: Flow<List<ConsignmentExpenseEntity>> = consignmentExpenseDao.getAllConsignmentExpenses()
    val partnerSetting: Flow<PartnerSettingEntity?> = partnerSettingDao.getSetting()

    // Transaction & Checkout Execution
    suspend fun checkout(
        cartItems: List<CartItem>,
        customerName: String,
        cashierName: String,
        paymentMethod: String,
        cashGiven: Double,
        cashChange: Double
    ): Result<TransactionEntity> = withContext(Dispatchers.IO) {
        try {
            val regularItems = cartItems.filter { !it.isConsignment }
            val consignmentItems = cartItems.filter { it.isConsignment }

            val now = Date()
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val dateStrFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val codeFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val baseCode = codeFormat.format(now)
            val randomSuffix = (1000..9999).random()

            var primaryTransaction: TransactionEntity? = null

            // 1. Process Regular Store Items
            if (regularItems.isNotEmpty()) {
                val totalRegular = regularItems.sumOf { it.subtotal }
                val trxId = "TRX-$baseCode-$randomSuffix"

                val transaction = TransactionEntity(
                    id = trxId,
                    baseCode = baseCode,
                    dateISO = isoFormat.format(now),
                    date = dateStrFormat.format(now),
                    customer = customerName.ifBlank { "Pelanggan Umum" },
                    cashier = cashierName,
                    method = paymentMethod,
                    total = totalRegular,
                    cashGiven = cashGiven,
                    cashChange = cashChange,
                    items = regularItems
                )
                transactionDao.insertTransaction(transaction)
                primaryTransaction = transaction

                // Deduct stock for each ingredient in product recipe (BOM)
                for (item in regularItems) {
                    val product = productDao.getProductById(item.id)
                    if (product != null) {
                        for (ing in product.ingredients) {
                            val totalDeduct = ing.amount * item.qty
                            stockItemDao.deductStock(ing.stockId, totalDeduct)
                        }
                    }
                }
            }

            // 2. Process Consignment Items
            if (consignmentItems.isNotEmpty()) {
                val totalConsign = consignmentItems.sumOf { it.subtotal }
                val consignTrxId = "CSG-$baseCode-$randomSuffix"

                val consignTrx = ConsignmentTransactionEntity(
                    id = consignTrxId,
                    baseCode = baseCode,
                    dateISO = isoFormat.format(now),
                    date = dateStrFormat.format(now),
                    customer = customerName.ifBlank { "Pelanggan Umum" },
                    cashier = cashierName,
                    method = paymentMethod,
                    total = totalConsign,
                    cashGiven = cashGiven,
                    cashChange = cashChange,
                    items = consignmentItems
                )
                consignmentTransactionDao.insertConsignmentTransaction(consignTrx)

                if (primaryTransaction == null) {
                    primaryTransaction = TransactionEntity(
                        id = consignTrxId,
                        baseCode = baseCode,
                        dateISO = consignTrx.dateISO,
                        date = consignTrx.date,
                        customer = consignTrx.customer,
                        cashier = consignTrx.cashier,
                        method = consignTrx.method,
                        total = consignTrx.total,
                        cashGiven = consignTrx.cashGiven,
                        cashChange = consignTrx.cashChange,
                        items = consignmentItems
                    )
                }

                // Deduct consignment inventory & increment soldQty
                for (item in consignmentItems) {
                    consignmentProductDao.deductConsignmentStock(item.id, item.qty.toDouble())
                }
            }

            if (primaryTransaction != null) {
                Result.success(primaryTransaction)
            } else {
                Result.failure(Exception("Keranjang belanja kosong"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Product CRUD
    suspend fun saveProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    // Stock CRUD
    suspend fun saveStockItem(item: StockItemEntity) = withContext(Dispatchers.IO) {
        stockItemDao.insertStockItem(item)
    }

    suspend fun addStock(stockId: String, amount: Double) = withContext(Dispatchers.IO) {
        stockItemDao.addStock(stockId, amount)
    }

    suspend fun deleteStockItem(item: StockItemEntity) = withContext(Dispatchers.IO) {
        stockItemDao.deleteStockItem(item)
    }

    // Consignment Product CRUD
    suspend fun saveConsignmentProduct(product: ConsignmentProductEntity) = withContext(Dispatchers.IO) {
        consignmentProductDao.insertConsignmentProduct(product)
    }

    suspend fun deleteConsignmentProduct(product: ConsignmentProductEntity) = withContext(Dispatchers.IO) {
        consignmentProductDao.deleteConsignmentProduct(product)
    }

    // Expense CRUD
    suspend fun addExpense(category: String, amount: Double, note: String) = withContext(Dispatchers.IO) {
        val now = Date()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val expense = ExpenseEntity(
            id = "exp-${UUID.randomUUID().toString().take(8)}",
            dateISO = isoFormat.format(now),
            time = timeFormat.format(now),
            category = category,
            amount = amount,
            note = note,
            timestamp = now.time
        )
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpense(expense)
    }

    // Consignment Expense (Payout to vendor)
    suspend fun addConsignmentExpense(vendor: String, amount: Double, note: String) = withContext(Dispatchers.IO) {
        val now = Date()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val expense = ConsignmentExpenseEntity(
            id = "csgexp-${UUID.randomUUID().toString().take(8)}",
            dateISO = isoFormat.format(now),
            time = timeFormat.format(now),
            category = "Bagi Hasil",
            vendor = vendor,
            amount = amount,
            note = note,
            timestamp = now.time
        )
        consignmentExpenseDao.insertConsignmentExpense(expense)
    }

    suspend fun deleteConsignmentExpense(expense: ConsignmentExpenseEntity) = withContext(Dispatchers.IO) {
        consignmentExpenseDao.deleteConsignmentExpense(expense)
    }

    // Partner Setting
    suspend fun savePartnerSetting(percent: Double) = withContext(Dispatchers.IO) {
        partnerSettingDao.saveSetting(PartnerSettingEntity("default", percent))
    }

    // Cloud Synchronization with Google Apps Script Web App
    suspend fun syncWithCloud(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiUrl = userPreferences.apiUrl.first()
            if (apiUrl.isBlank()) return@withContext Result.failure(Exception("URL API Google Sheets belum diatur"))

            val token = userPreferences.authToken.first()

            val allStocks = stockItems.first()
            val allProducts = products.first()
            val allConsignment = consignmentProducts.first()
            val allTrx = transactions.first()
            val allConsignTrx = consignmentTransactions.first()
            val allExp = expenses.first()
            val allConsignExp = consignmentExpenses.first()
            val setting = partnerSetting.first()

            val payload = CollectionsPayload(
                stockItems = allStocks,
                products = allProducts,
                consignmentProducts = allConsignment,
                transactions = allTrx,
                consignmentTransactions = allConsignTrx,
                expenses = allExp,
                consignmentExpenses = allConsignExp,
                partnerSettings = if (setting != null) listOf(setting) else null
            )

            val request = BaseApiRequest(
                action = "sync",
                token = token,
                collections = payload
            )

            val response = ApiClient.apiService.sendRequest(apiUrl, request)
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: ""
                val type = object : TypeToken<BaseApiResponse<Any>>() {}.type
                val apiRes: BaseApiResponse<Any> = gson.fromJson(bodyStr, type)
                if (apiRes.ok) {
                    Result.success("Sinkronisasi Cloud Berhasil!")
                } else {
                    Result.failure(Exception(apiRes.error ?: "Gagal sinkronisasi ke cloud"))
                }
            } else {
                Result.failure(Exception("HTTP Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
