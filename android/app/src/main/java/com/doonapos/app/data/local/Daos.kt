package com.doonapos.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StockItemDao {
    @Query("SELECT * FROM stock_items ORDER BY name ASC")
    fun getAllStockItems(): Flow<List<StockItemEntity>>

    @Query("SELECT * FROM stock_items WHERE id = :id")
    suspend fun getStockItemById(id: String): StockItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockItem(item: StockItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StockItemEntity>)

    @Update
    suspend fun updateStockItem(item: StockItemEntity)

    @Delete
    suspend fun deleteStockItem(item: StockItemEntity)

    @Query("UPDATE stock_items SET qty = qty - :amount WHERE id = :id")
    suspend fun deductStock(id: String, amount: Double)

    @Query("UPDATE stock_items SET qty = qty + :amount WHERE id = :id")
    suspend fun addStock(id: String, amount: Double)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}

@Dao
interface ConsignmentProductDao {
    @Query("SELECT * FROM consignment_products ORDER BY name ASC")
    fun getAllConsignmentProducts(): Flow<List<ConsignmentProductEntity>>

    @Query("SELECT * FROM consignment_products WHERE id = :id")
    suspend fun getConsignmentProductById(id: String): ConsignmentProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsignmentProduct(product: ConsignmentProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ConsignmentProductEntity>)

    @Update
    suspend fun updateConsignmentProduct(product: ConsignmentProductEntity)

    @Delete
    suspend fun deleteConsignmentProduct(product: ConsignmentProductEntity)

    @Query("UPDATE consignment_products SET qty = qty - :qty, soldQty = soldQty + :qty WHERE id = :id")
    suspend fun deductConsignmentStock(id: String, qty: Double)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?
}

@Dao
interface ConsignmentTransactionDao {
    @Query("SELECT * FROM consignment_transactions ORDER BY timestamp DESC")
    fun getAllConsignmentTransactions(): Flow<List<ConsignmentTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsignmentTransaction(transaction: ConsignmentTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<ConsignmentTransactionEntity>)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
}

@Dao
interface ConsignmentExpenseDao {
    @Query("SELECT * FROM consignment_expenses ORDER BY timestamp DESC")
    fun getAllConsignmentExpenses(): Flow<List<ConsignmentExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsignmentExpense(expense: ConsignmentExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ConsignmentExpenseEntity>)

    @Delete
    suspend fun deleteConsignmentExpense(expense: ConsignmentExpenseEntity)
}

@Dao
interface PartnerSettingDao {
    @Query("SELECT * FROM partner_settings WHERE id = 'default' LIMIT 1")
    fun getSetting(): Flow<PartnerSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: PartnerSettingEntity)
}
