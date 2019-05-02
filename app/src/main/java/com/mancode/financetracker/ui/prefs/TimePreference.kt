package com.mancode.financetracker.ui.prefs

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import androidx.preference.DialogPreference
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.format.FormatStyle

class TimePreference(context: Context, attrs: AttributeSet)
    : DialogPreference(context, attrs) {

    var time: LocalTime = LocalTime.of(20, 0)
        set(value) {
            field = value
            persistString(time.format(DateTimeFormatter.ISO_LOCAL_TIME))
            notifyDependencyChange(shouldDisableDependents())
            notifyChanged()
            updateSummary()
        }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any? = a?.getString(index)

    override fun onSetInitialValue(defaultValue: Any?) {
        val timeString: String = getPersistedString(defaultValue as String?)
        try {
            val timeParsed: LocalTime = LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME)
            time = timeParsed
        } catch (e: DateTimeParseException) {
            Log.d("", "$timeString could not be parsed with ISO_LOCAL_DATE Formatter")
        }
    }

    private fun updateSummary() {
        summary = time.format(DateTimeFormatter
                .ofLocalizedTime(FormatStyle.SHORT)
        )
    }

}