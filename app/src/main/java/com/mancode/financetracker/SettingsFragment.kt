package com.mancode.financetracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mancode.financetracker.notifications.CHANNEL_ID_REMINDER
import com.mancode.financetracker.notifications.cancelTransactionReminder
import com.mancode.financetracker.notifications.registerTransactionReminder
import com.mancode.financetracker.ui.MainActivityNav
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.ui.prefs.TimePreference
import com.mancode.financetracker.ui.prefs.TimePreferenceDialogFragment
import com.mancode.financetracker.utils.checkPermissionAndExport
import com.mancode.financetracker.utils.checkPermissionAndImport
import kotlinx.android.synthetic.main.fragment_settings.*
import org.joda.money.CurrencyUnit
import org.threeten.bp.LocalTime

class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val transactionReminderWeekdaysPref =
                findPreference<MultiSelectListPreference>(getString(R.string.transaction_reminder_weekdays))
        transactionReminderWeekdaysPref?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValues ->
            transactionReminderWeekdaysPref?.setSummaryFromValues(newValues as Set<String>)
            true
        }
        transactionReminderWeekdaysPref?.setSummaryFromValues()

        val defaultCurrencyPref = findPreference<ListPreference>(getString(R.string.default_currency))
        with(defaultCurrencyPref as ListPreference) {
            entries = CurrencyUnit.registeredCurrencies().map { it.code }.toTypedArray()
            entryValues = entries
            value = PreferenceAccessor.defaultCurrency
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }

        val exportPref = findPreference<Preference>("key_export")
        exportPref?.setOnPreferenceClickListener {
            (activity as MainActivityNav).checkPermissionAndExport()
            true
        }
        val importPref = findPreference<Preference>("key_import")
        importPref?.setOnPreferenceClickListener {
            (activity as MainActivityNav).checkPermissionAndImport()
            true
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfig = AppBarConfiguration(navController.graph)
        settingsToolbar.setupWithNavController(navController, appBarConfig)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val keyTransactionReminderEnabled = getString(R.string.transaction_reminder_enabled)
        val keyTransactionReminderTime = getString(R.string.transaction_reminder_time)
        if (key == keyTransactionReminderEnabled || key == keyTransactionReminderTime) {
            val enabled = sharedPreferences.getBoolean(keyTransactionReminderEnabled, false)
            if (enabled) {
                val time = findPreference<TimePreference>(keyTransactionReminderTime)?.time
                        ?: LocalTime.of(20, 0)
                registerTransactionReminder(context, time)
            } else cancelTransactionReminder(context)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is TimePreference) {
            val fragment = TimePreferenceDialogFragment.newInstance(preference.key)
            fragment.setTargetFragment(this, 0)
            fragment.show(parentFragmentManager, null)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    private fun MultiSelectListPreference.setSummaryFromValues(newValues: Set<String> = values) {
        summary = newValues.joinToString { entries[findIndexOfValue(it)].substring(0, 3) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name_transaction_input_reminder)
            val desc = getString(R.string.channel_desc_transaction_input_reminder)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_REMINDER, name, importance)
            channel.description = desc

            val notificationManager = getSystemService(context!!, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
