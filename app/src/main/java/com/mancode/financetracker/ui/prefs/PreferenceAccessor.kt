package com.mancode.financetracker.ui.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mancode.financetracker.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

object PreferenceAccessor {

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var ratesFetchDate: LocalDate?
        get() {
            val string = preferences.getString("rates_fetch_date", null)
            return if (string != null) LocalDate.parse(string) else null
        }
        set(value) = preferences.edit { it.putString("rates_fetch_date", value.toString()) }

    var transactionReminderDecided: Boolean
        get() = preferences.getBoolean("transaction_reminder_decided", false)
        set(value) = preferences.edit { it.putBoolean("transaction_reminder_decided", value) }

    var transactionReminderEnabled: Boolean
        get() = preferences.getBoolean("transaction_reminder_enabled", false)
        set(value) = preferences.edit { it.putBoolean("transaction_reminder_enabled", value) }

    var transactionReminderTime: LocalTime
        get() {
            val string = preferences.getString("transaction_reminder_time", "20:00:00")!!
            return LocalTime.parse(string)
        }
        set(value) = preferences.edit { it.putString("transaction_reminder_time", value.toString()) }

    var transactionReminderWeekdays: Set<String>
        get() = preferences.getStringSet("transaction_reminder_weekdays", emptySet())!!
        set(value) = preferences.edit { it.putStringSet("transaction_reminder_time", value) }

    fun isTransactionReminderValid(): Boolean = transactionReminderEnabled &&
            transactionReminderWeekdays.contains(LocalDate.now().dayOfWeek.value.toString())

}