package com.mancode.financetracker.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.mancode.financetracker.R
import com.mancode.financetracker.SettingsActivity
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
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

fun resetRemindersAndShowDecisionDialog(context: Context) {
    if (PreferenceAccessor.transactionReminderDecided) {
        if (PreferenceAccessor.transactionReminderEnabled) {
            registerReminderWithProperty(context)
        } else {
            cancelTransactionReminder(context)
        }
        return
    }

    val builder = AlertDialog.Builder(context)
    builder.setMessage(context.getString(R.string.question_transaction_reminder_decision))
            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                registerReminderWithProperty(context)
                val intent = Intent(context, SettingsActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            }
            .setNegativeButton(context.getString(R.string.no)) { _, _ ->
                PreferenceAccessor.transactionReminderDecided = true
                PreferenceAccessor.transactionReminderEnabled = false
                cancelTransactionReminder(context)
            }
            .create().show()
}

fun registerReminderWithProperty(context: Context) {
    if (!PreferenceAccessor.transactionReminderEnabled) return

    val time = PreferenceAccessor.transactionReminderTime
    registerTransactionReminder(context, time)
}
