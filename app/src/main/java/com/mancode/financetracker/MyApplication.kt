package com.mancode.financetracker

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceAccessor.init(this)
        Timber.plant(Timber.DebugTree())
    }
}