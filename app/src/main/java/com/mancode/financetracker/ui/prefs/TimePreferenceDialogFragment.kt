package com.mancode.financetracker.ui.prefs

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import com.mancode.financetracker.R
import org.threeten.bp.LocalTime

class TimePreferenceDialogFragment : PreferenceDialogFragmentCompat(),
        TimePickerDialog.OnTimeSetListener{
    private val picker: TimePicker by lazy { TimePicker(context) }
    private lateinit var lastTime: LocalTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lastTime = (preference as TimePreference).time
    }

    override fun onCreateDialogView(context: Context?): View {
        picker.setIs24HourView(DateFormat.is24HourFormat(context))
        return picker
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            picker.currentHour = lastTime.hour
            picker.currentMinute = lastTime.minute
        } else {
            picker.hour = lastTime.hour
            picker.minute = lastTime.minute
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(
                context,
                R.style.AppTheme_Picker,
                this,
                lastTime.hour,
                lastTime.minute,
                DateFormat.is24HourFormat(context))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        lastTime = LocalTime.of(hourOfDay, minute)
        (preference as TimePreference).time = lastTime
    }

    override fun onDialogClosed(positiveResult: Boolean) {
    }

    companion object {
        fun newInstance(key: String) = TimePreferenceDialogFragment().apply {
            arguments = Bundle(2).apply {
                putString(ARG_KEY, key)
            }
        }
    }
}

