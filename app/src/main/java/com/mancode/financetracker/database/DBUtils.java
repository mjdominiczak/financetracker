package com.mancode.financetracker.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Manveru on 08.11.2017.
 */

public class DBUtils {

    public static String formatDate(Date date) {
        return date != null ?
                new SimpleDateFormat(DatabaseContract.DATE_FORMAT_STRING, Locale.US).format(date) : null;
    }
}
