package com.mancode.financetracker.database.converter;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Manveru on 02.02.2018.
 */

public class DateConverter {

    private static final String TAG = DateConverter.class.getName();

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    @TypeConverter
    public static Date toDate(String string) {
        if (string == null) return null;
        Date result = null;
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.getDefault());
            result = df.parse(string);
        } catch (ParseException e) {
            Log.d(TAG, "Error when trying to convert string [" + string + "] to date");
        }
        return result;
    }

    @TypeConverter
    public static String toString(Date date) {
        return date == null ? null :
                new SimpleDateFormat(DATE_FORMAT_STRING, Locale.US).format(date);
    }
}
