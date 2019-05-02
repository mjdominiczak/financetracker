package com.mancode.financetracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mancode.financetracker.notifications.CHANNEL_ID_REMINDER
import com.mancode.financetracker.notifications.cancelTransactionReminder
import com.mancode.financetracker.notifications.registerTransactionReminder
import com.mancode.financetracker.ui.prefs.TimePreference
import com.mancode.financetracker.ui.prefs.TimePreferenceDialogFragment
import org.threeten.bp.LocalTime

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.action_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name_transaction_input_reminder)
            val desc = getString(R.string.channel_desc_transaction_input_reminder)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_REMINDER, name, importance)
            channel.description = desc

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

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

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val transactionReminderWeekdaysPref =
                    findPreference<MultiSelectListPreference>(getString(R.string.transaction_reminder_weekdays))
            transactionReminderWeekdaysPref?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValues ->
                transactionReminderWeekdaysPref?.setSummaryFromValues(newValues as Set<String>)
                true
            }
            transactionReminderWeekdaysPref?.setSummaryFromValues()
            return super.onCreateView(inflater, container, savedInstanceState)
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
                fragment.show(fragmentManager!!, null)
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }

        private fun MultiSelectListPreference.setSummaryFromValues(newValues: Set<String> = values) {
            summary = newValues.joinToString { entries[findIndexOfValue(it)].substring(0, 3) }
        }

    }

}

