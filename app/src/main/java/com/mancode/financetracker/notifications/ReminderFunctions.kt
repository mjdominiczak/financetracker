package com.mancode.financetracker.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime

const val CHANNEL_ID_REMINDER = "channel_reminder"

fun registerTransactionReminder(context: Context?, time: LocalTime) {
    if (context == null) return

    val pendingIntent = constructPendingIntent(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val dateTime: ZonedDateTime = if (time.isBefore(ZonedDateTime.now().toLocalTime())) {
        ZonedDateTime.now().with(time).plusDays(1)
    } else {
        ZonedDateTime.now().with(time)
    }

    alarmManager.cancel(pendingIntent)
    alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            dateTime.toInstant().toEpochMilli(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent)
}

fun cancelTransactionReminder(context: Context?) {
    if (context == null) return

    val pendingIntent = constructPendingIntent(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

private fun constructPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, ReminderNotificationReceiver::class.java)
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

