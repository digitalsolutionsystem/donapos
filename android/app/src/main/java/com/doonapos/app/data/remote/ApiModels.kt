package com.doonapos.app.data.remote

import com.doonapos.app.data.local.*
import com.google.gson.annotations.SerializedName

data class BaseApiRequest(
    @SerializedName("action") val action: String,
    @SerializedName("token") val token: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("pin") val pin: String? = null,
    @SerializedName("collections") val collections: CollectionsPayload? = null
)

data class CollectionsPayload(
    @SerializedName("stockItems") val stockItems: List<StockItemEntity>? = null,
    @SerializedName("products") val products: List<ProductEntity>? = null,
    @SerializedName("consignmentProducts") val consignmentProducts: List<ConsignmentProductEntity>? = null,
    @SerializedName("transactions") val transactions: List<TransactionEntity>? = null,
    @SerializedName("consignmentTransactions") val consignmentTransactions: List<ConsignmentTransactionEntity>? = null,
    @SerializedName("expenses") val expenses: List<ExpenseEntity>? = null,
    @SerializedName("consignmentExpenses") val consignmentExpenses: List<ConsignmentExpenseEntity>? = null,
    @SerializedName("consignmentPartnerSettings") val partnerSettings: List<PartnerSettingEntity>? = null
)

data class BaseApiResponse<T>(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("code") val code: String? = null
)

data class LoginData(
    @SerializedName("token") val token: String,
    @SerializedName("expiresAt") val expiresAt: Long,
    @SerializedName("user") val user: UserInfo
)

data class UserInfo(
    @SerializedName("role") val role: String,
    @SerializedName("name") val name: String
)

data class BootstrapData(
    @SerializedName("stockItems") val stockItems: List<StockItemEntity> = emptyList(),
    @SerializedName("products") val products: List<ProductEntity> = emptyList(),
    @SerializedName("consignmentProducts") val consignmentProducts: List<ConsignmentProductEntity> = emptyList(),
    @SerializedName("transactions") val transactions: List<TransactionEntity> = emptyList(),
    @SerializedName("consignmentTransactions") val consignmentTransactions: List<ConsignmentTransactionEntity> = emptyList(),
    @SerializedName("expenses") val expenses: List<ExpenseEntity> = emptyList(),
    @SerializedName("consignmentExpenses") val consignmentExpenses: List<ConsignmentExpenseEntity> = emptyList(),
    @SerializedName("consignmentPartnerSettings") val partnerSettings: List<PartnerSettingEntity> = emptyList()
)
