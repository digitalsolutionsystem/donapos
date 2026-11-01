package com.doonapos.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.doonapos.app.data.model.CartItem
import com.doonapos.app.data.model.Ingredient
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stock_items")
data class StockItemEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("unit") val unit: String,
    @SerializedName("qty") val qty: Double,
    @SerializedName("minLimit") val minLimit: Double,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Double,
    @SerializedName("image") val image: String,
    @SerializedName("ingredients") val ingredients: List<Ingredient> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "consignment_products")
data class ConsignmentProductEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("vendor") val vendor: String,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Double,
    @SerializedName("storePercent") val storePercent: Double = 20.0,
    @SerializedName("costPrice") val costPrice: Double = 0.0,
    @SerializedName("qty") val qty: Double = 0.0,
    @SerializedName("soldQty") val soldQty: Double = 0.0,
    @SerializedName("minLimit") val minLimit: Double = 5.0,
    @SerializedName("image") val image: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("baseCode") val baseCode: String = "",
    @SerializedName("dateISO") val dateISO: String = "",
    @SerializedName("date") val date: String = "",
    @SerializedName("customer") val customer: String = "Pelanggan Umum",
    @SerializedName("cashier") val cashier: String = "Kasir 1",
    @SerializedName("method") val method: String = "Tunai",
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("cashGiven") val cashGiven: Double = 0.0,
    @SerializedName("cashChange") val cashChange: Double = 0.0,
    @SerializedName("items") val items: List<CartItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "consignment_transactions")
data class ConsignmentTransactionEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("baseCode") val baseCode: String = "",
    @SerializedName("dateISO") val dateISO: String = "",
    @SerializedName("date") val date: String = "",
    @SerializedName("customer") val customer: String = "Pelanggan Umum",
    @SerializedName("cashier") val cashier: String = "Kasir 1",
    @SerializedName("method") val method: String = "Tunai",
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("cashGiven") val cashGiven: Double = 0.0,
    @SerializedName("cashChange") val cashChange: Double = 0.0,
    @SerializedName("items") val items: List<CartItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("dateISO") val dateISO: String = "",
    @SerializedName("time") val time: String = "",
    @SerializedName("category") val category: String = "Operasional",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("note") val note: String = "",
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "consignment_expenses")
data class ConsignmentExpenseEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("dateISO") val dateISO: String = "",
    @SerializedName("time") val time: String = "",
    @SerializedName("category") val category: String = "Bagi Hasil",
    @SerializedName("vendor") val vendor: String = "",
    @SerializedName("amount") val amount: Double = 0.0,
    @SerializedName("note") val note: String = "",
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "partner_settings")
data class PartnerSettingEntity(
    @PrimaryKey
    @SerializedName("id") val id: String = "default",
    @SerializedName("storePercent") val storePercent: Double = 20.0
)
