package com.mancode.financetracker.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.mancode.financetracker.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notification = Notification.Builder(context)
                .setContentTitle("FT Reminder")
                .setContentText("Any transactions to enter?")
                .setSmallIcon(R.drawable.ic_launcher_mono)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.notify(0, notification)
    }
}
