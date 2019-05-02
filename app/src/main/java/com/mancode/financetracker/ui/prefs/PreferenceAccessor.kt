package com.mancode.financetracker.ui.prefs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import com.mancode.financetracker.R
import com.mancode.financetracker.SettingsActivity
import com.mancode.financetracker.notifications.cancelTransactionReminder
import com.mancode.financetracker.notifications.registerTransactionReminder
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException

class PreferenceAccessor(val context: Context) {

    init {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
    }

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context) }
    private val transactionReminderDecided by lazy {
        preferences.getBoolean(context.getString(R.string.transaction_reminder_decided), false) }
    private val transactionReminderEnabled by lazy {
        preferences.getBoolean(context.getString(R.string.transaction_reminder_enabled), false) }
    private val timeString by lazy { preferences
            .getString(context.getString(R.string.transaction_reminder_time), "20:00") }
    private val transactionReminderWeekdays by lazy { preferences
            .getStringSet(context.getString(R.string.transaction_reminder_weekdays), null) }

    fun isTransactionReminderValid() : Boolean = transactionReminderEnabled &&
            (transactionReminderWeekdays?.contains(LocalDate.now().dayOfWeek.value.toString()) ?: false)

    fun resetRemindersAndShowDecisionDialog() {
        if (transactionReminderDecided) {
            if (transactionReminderEnabled) {
                registerReminderWithProperty()
            } else {
                cancelTransactionReminder(context)
            }
            return
        }

        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.question_transaction_reminder_decision))
                .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                    preferences.edit()
                            .putBoolean(context.getString(R.string.transaction_reminder_decided), true)
                            .putBoolean(context.getString(R.string.transaction_reminder_enabled), true)
                            .apply()
                    registerReminderWithProperty()
                    val intent = Intent(context, SettingsActivity::class.java)
                    startActivity(context, intent, null)
                }
                .setNegativeButton(context.getString(R.string.no)) { _, _ ->
                    preferences.edit()
                            .putBoolean(context.getString(R.string.transaction_reminder_decided), true)
                            .putBoolean(context.getString(R.string.transaction_reminder_enabled), false)
                            .apply()
                    cancelTransactionReminder(context)
                }
                .create().show()
    }

    private fun registerReminderWithProperty() {
        try {
            assert(timeString != null)
            val time = LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME)
            registerTransactionReminder(context, time)
        } catch (e: DateTimeParseException) {
            Log.d("PreferenceAccessor", String.format("%s could not be parsed with ISO_LOCAL_TIME formatter", timeString))
        }
    }

}