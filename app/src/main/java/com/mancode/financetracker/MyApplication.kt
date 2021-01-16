package com.mancode.financetracker

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceAccessor.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR || priority == Log.DEBUG) {
                FirebaseCrashlytics.getInstance().log(message)
                if (t != null) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                }
            }
        }
    }
}