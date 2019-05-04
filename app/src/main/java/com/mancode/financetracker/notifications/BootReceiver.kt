package com.mancode.financetracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mancode.financetracker.ui.prefs.PreferenceAccessor

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != null && action == Intent.ACTION_BOOT_COMPLETED) {
            val prefAccessor = PreferenceAccessor(context)
            prefAccessor.registerReminderWithProperty()
            Log.d("BootReceiver", "Reminder registered")
        }
    }

}