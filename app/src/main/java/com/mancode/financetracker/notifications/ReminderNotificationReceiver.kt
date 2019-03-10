package com.mancode.financetracker.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mancode.financetracker.MainActivity
import com.mancode.financetracker.MyTabsPagerAdapter
import com.mancode.financetracker.R

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_VISIBLE_FRAGMENT, MyTabsPagerAdapter.TRANSACTION_FRAGMENT)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID_REMINDER)
                .setContentTitle("FT Reminder")
                .setContentText(context.getString(R.string.notification_text_reminder))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_mono)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(0, notification)
    }
}
