package com.doonapos.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StockItemEntity::class,
        ProductEntity::class,
        ConsignmentProductEntity::class,
        TransactionEntity::class,
        ConsignmentTransactionEntity::class,
        ExpenseEntity::class,
        ConsignmentExpenseEntity::class,
        PartnerSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockItemDao(): StockItemDao
    abstract fun productDao(): ProductDao
    abstract fun consignmentProductDao(): ConsignmentProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun consignmentTransactionDao(): ConsignmentTransactionDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun consignmentExpenseDao(): ConsignmentExpenseDao
    abstract fun partnerSettingDao(): PartnerSettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "doonapos.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial dummy data as in donapos.html
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).seedInitialData()
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        val initialStocks = listOf(
            StockItemEntity("stk-1", "Biji Kopi Arabica Gayo", "gram", 2500.0, 500.0),
            StockItemEntity("stk-2", "Susu Pasteurisasi Fresh", "ml", 6000.0, 1000.0),
            StockItemEntity("stk-3", "Gula Aren Cair Nira", "ml", 1500.0, 300.0),
            StockItemEntity("stk-4", "Paper Cup Hot/Cold 8oz", "pcs", 85.0, 20.0),
            StockItemEntity("stk-5", "Uji Matcha Pure Powder", "gram", 800.0, 200.0),
            StockItemEntity("stk-6", "Artisan Brioche Bun", "pcs", 25.0, 10.0),
            StockItemEntity("stk-7", "Smash Beef Patty 110g", "pcs", 20.0, 8.0),
            StockItemEntity("stk-8", "Sharp Cheddar Slice", "lembar", 40.0, 15.0),
            StockItemEntity("stk-9", "Greaseproof Food Wrap", "pcs", 90.0, 25.0)
        )
        stockItemDao().insertAll(initialStocks)

        val initialProducts = listOf(
            ProductEntity(
                id = "prd-1",
                name = "Kopi Susu Aren Nira",
                category = "Kopi",
                price = 22000.0,
                image = "https://images.unsplash.com/photo-1541167760496-1628856ab772?auto=format&fit=crop&w=400&q=80",
                ingredients = listOf(
                    com.doonapos.app.data.model.Ingredient("stk-1", 18.0),
                    com.doonapos.app.data.model.Ingredient("stk-2", 120.0),
                    com.doonapos.app.data.model.Ingredient("stk-3", 25.0),
                    com.doonapos.app.data.model.Ingredient("stk-4", 1.0)
                )
            ),
            ProductEntity(
                id = "prd-2",
                name = "Double Espresso Doppio",
                category = "Kopi",
                price = 18000.0,
                image = "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?auto=format&fit=crop&w=400&q=80",
                ingredients = listOf(
                    com.doonapos.app.data.model.Ingredient("stk-1", 18.0)
                )
            ),
            ProductEntity(
                id = "prd-3",
                name = "Kyoto Ceremonial Matcha Latte",
                category = "Non-Kopi",
                price = 26000.0,
                image = "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?auto=format&fit=crop&w=400&q=80",
                ingredients = listOf(
                    com.doonapos.app.data.model.Ingredient("stk-5", 15.0),
                    com.doonapos.app.data.model.Ingredient("stk-2", 150.0),
                    com.doonapos.app.data.model.Ingredient("stk-3", 15.0),
                    com.doonapos.app.data.model.Ingredient("stk-4", 1.0)
                )
            ),
            ProductEntity(
                id = "prd-4",
                name = "Craft Smash Burger",
                category = "Makanan",
                price = 38000.0,
                image = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=400&q=80",
                ingredients = listOf(
                    com.doonapos.app.data.model.Ingredient("stk-6", 1.0),
                    com.doonapos.app.data.model.Ingredient("stk-7", 1.0),
                    com.doonapos.app.data.model.Ingredient("stk-8", 1.0),
                    com.doonapos.app.data.model.Ingredient("stk-9", 1.0)
                )
            )
        )
        productDao().insertAll(initialProducts)

        val initialConsignment = listOf(
            ConsignmentProductEntity(
                id = "csg-1",
                name = "Croissant Almond Paris",
                vendor = "Bakery Mawar",
                category = "Pastry",
                price = 25000.0,
                storePercent = 20.0,
                costPrice = 20000.0,
                qty = 15.0,
                soldQty = 0.0,
                minLimit = 3.0,
                image = "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=400&q=80"
            ),
            ConsignmentProductEntity(
                id = "csg-2",
                name = "Fudgy Brownies Shiny Crust",
                vendor = "Dapur Bunda",
                category = "Dessert",
                price = 28000.0,
                storePercent = 15.0,
                costPrice = 23800.0,
                qty = 10.0,
                soldQty = 0.0,
                minLimit = 2.0,
                image = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=400&q=80"
            )
        )
        consignmentProductDao().insertAll(initialConsignment)
        partnerSettingDao().saveSetting(PartnerSettingEntity("default", 20.0))
    }
}
