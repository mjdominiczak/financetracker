package com.mancode.financetracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.mancode.financetracker.R
import com.mancode.financetracker.ui.prefs.PreferenceAccessor

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!PreferenceAccessor.isTransactionReminderValid())
            return

        val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.addEditTransactionFragment)
                .createPendingIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDER)
                .setContentTitle(context.getString(R.string.notification_title_reminder))
                .setContentText(context.getString(R.string.notification_text_reminder))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_mono)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .build()

        NotificationManagerCompat.from(context).notify(0, notification)
    }
}
