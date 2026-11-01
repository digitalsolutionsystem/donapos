package com.doonapos.app

import android.app.Application
import com.doonapos.app.data.local.AppDatabase

class DoonaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Room DB
        AppDatabase.getInstance(this)
    }
}
