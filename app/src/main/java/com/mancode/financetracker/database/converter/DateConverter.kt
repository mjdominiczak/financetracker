package com.mancode.financetracker.database.converter

import android.util.Log
import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Manveru on 02.02.2018.
 */

object DateConverter {

    private val TAG = DateConverter::class.java.name

    const val DATE_FORMAT_STRING = "yyyy-MM-dd"

    @TypeConverter
    fun toDate(string: String?): Date? {
        if (string == null || string.isEmpty()) return null
        var result: Date? = null
        try {
            val df = SimpleDateFormat(DATE_FORMAT_STRING, Locale.getDefault())
            result = df.parse(string)
        } catch (e: ParseException) {
            Log.d(TAG, "Error when trying to convert string [$string] to date")
        }

        return result
    }

    @TypeConverter
    fun toString(date: Date?): String? {
        return if (date == null)
            null
        else
            SimpleDateFormat(DATE_FORMAT_STRING, Locale.US).format(date)
    }
}
