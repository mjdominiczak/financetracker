package com.mancode.financetracker

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mancode.financetracker.ui.prefs.PreferenceAccessor

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceAccessor.init(this)
    }
}