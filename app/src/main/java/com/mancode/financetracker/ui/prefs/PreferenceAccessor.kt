package com.mancode.financetracker.ui.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.mancode.financetracker.R
import org.joda.money.CurrencyUnit
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*

object PreferenceAccessor {

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var defaultCurrency: String
        get() = preferences.getString("default_currency",
                CurrencyUnit.of(Locale.getDefault()).code)!!
        set(value) = preferences.edit { putString("default_currency", value) }

    var firstRun: Boolean
        get() = preferences.getBoolean("first_run", true)
        set(value) = preferences.edit { putBoolean("first_run", value) }

    var lastUpdateDate: LocalDate?
        get() {
            val string = preferences.getString("last_update_date", null)
            return if (string != null) LocalDate.parse(string) else null
        }
        set(value) = preferences.edit { putString("last_update_date", value.toString()) }

    var ratesFetchDate: LocalDate?
        get() {
            val string = preferences.getString("rates_fetch_date", null)
            return if (string != null) LocalDate.parse(string) else null
        }
        set(value) = preferences.edit { putString("rates_fetch_date", value.toString()) }

    var transactionReminderDecided: Boolean
        get() = preferences.getBoolean("transaction_reminder_decided", false)
        set(value) = preferences.edit { putBoolean("transaction_reminder_decided", value) }

    var transactionReminderEnabled: Boolean
        get() = preferences.getBoolean("transaction_reminder_enabled", false)
        set(value) = preferences.edit { putBoolean("transaction_reminder_enabled", value) }

    var transactionReminderTime: LocalTime
        get() {
            val string = preferences.getString("transaction_reminder_time", "20:00:00")!!
            return LocalTime.parse(string)
        }
        set(value) = preferences.edit { putString("transaction_reminder_time", value.toString()) }

    var transactionReminderWeekdays: Set<String>
        get() = preferences.getStringSet("transaction_reminder_weekdays", emptySet())!!
        set(value) = preferences.edit { putStringSet("transaction_reminder_time", value) }

    fun isTransactionReminderValid(): Boolean = transactionReminderEnabled &&
            transactionReminderWeekdays.contains(LocalDate.now().dayOfWeek.value.toString())

}