package com.doonapos.app.data.local

import androidx.room.TypeConverter
import com.doonapos.app.data.model.CartItem
import com.doonapos.app.data.model.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(value: List<Ingredient>?): String {
        return gson.toJson(value ?: emptyList<Ingredient>())
    }

    @TypeConverter
    fun toIngredientList(value: String?): List<Ingredient> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return try {
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromCartItemList(value: List<CartItem>?): String {
        return gson.toJson(value ?: emptyList<CartItem>())
    }

    @TypeConverter
    fun toCartItemList(value: String?): List<CartItem> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return try {
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
