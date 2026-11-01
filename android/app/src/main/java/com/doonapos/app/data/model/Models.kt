package com.doonapos.app.data.model

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("stockId") val stockId: String = "",
    @SerializedName("amount") val amount: Double = 0.0
)

data class CartItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val qty: Int = 1,
    val isConsignment: Boolean = false,
    val vendor: String? = null,
    val storePercent: Double = 0.0,
    val costPrice: Double = 0.0,
    val image: String = "",
    val ingredients: List<Ingredient> = emptyList()
) {
    val subtotal: Double get() = price * qty
}

data class UserSession(
    val role: String = "kasir", // "kasir" or "owner"
    val name: String = "Kasir 1",
    val token: String? = null
)
