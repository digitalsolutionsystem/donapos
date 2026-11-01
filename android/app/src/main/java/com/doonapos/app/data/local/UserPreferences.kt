package com.doonapos.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "doonapos_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val KEY_API_URL = stringPreferencesKey("api_url")
        val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        val KEY_USER_ROLE = stringPreferencesKey("user_role")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_KASIR_PIN = stringPreferencesKey("kasir_pin")
        val KEY_OWNER_PIN = stringPreferencesKey("owner_pin")

        const val DEFAULT_API_URL = "https://script.google.com/macros/s/AKfycbwD_RYC8CsTLg-q37m9ppWkQBKecDuk7rB0e9Vay1mH6DHpNGCSQyt4Mt0LQBHGIlffHQ/exec"
        const val DEFAULT_KASIR_PIN = "1234"
        const val DEFAULT_OWNER_PIN = "8888"
    }

    val apiUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_API_URL] ?: DEFAULT_API_URL
    }

    val userRole: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ROLE]
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: "Kasir 1"
    }

    val authToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTH_TOKEN]
    }

    val kasirPin: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_KASIR_PIN] ?: DEFAULT_KASIR_PIN
    }

    val ownerPin: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_OWNER_PIN] ?: DEFAULT_OWNER_PIN
    }

    suspend fun saveSession(role: String, name: String, token: String?) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ROLE] = role
            prefs[KEY_USER_NAME] = name
            if (token != null) prefs[KEY_AUTH_TOKEN] = token else prefs.remove(KEY_AUTH_TOKEN)
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ROLE)
            prefs.remove(KEY_AUTH_TOKEN)
        }
    }

    suspend fun updateApiUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_API_URL] = url
        }
    }

    suspend fun updatePin(role: String, pin: String) {
        context.dataStore.edit { prefs ->
            if (role == "owner") {
                prefs[KEY_OWNER_PIN] = pin
            } else {
                prefs[KEY_KASIR_PIN] = pin
            }
        }
    }
}
