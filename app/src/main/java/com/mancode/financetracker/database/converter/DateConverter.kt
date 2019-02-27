package com.mancode.financetracker.database.converter

import android.util.Log
import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeParseException

/**
 * Created by Manveru on 02.02.2018.
 */

object DateConverter {

    private val TAG = DateConverter::class.java.name

    /**
     * Same as DateTimeFormatter.ISO_LOCAL_DATE
     */
    const val DATE_FORMAT_STRING = "yyyy-MM-dd"

    @TypeConverter
    @JvmStatic
    fun toDate(string: String?): LocalDate? {
        if (string == null || string.isEmpty()) return null
        var result: LocalDate? = null
        try {
            result = LocalDate.parse(string)
        } catch (e: DateTimeParseException) {
            Log.d(TAG, "Error when trying to convert string [$string] to date")
        }

        return result
    }

    @TypeConverter
    @JvmStatic
    fun toString(date: LocalDate?): String? {
        return date?.toString()
    }
}
