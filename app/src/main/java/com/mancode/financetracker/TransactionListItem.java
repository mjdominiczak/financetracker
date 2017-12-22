package com.mancode.financetracker;

import android.database.Cursor;
import android.util.Log;

import com.mancode.financetracker.database.DatabaseContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Manveru on 18.12.2017.
 */

public class TransactionListItem {

    private static final String TAG = TransactionListItem.class.getSimpleName();

    private int id;
    private final String transactionDate;
    private final int transactionType;
    private final String description;
    private final String accountName;
    private final double value;
    private final String category;

    public int getId() {
        return id;
    }

    public String getTransactionDate() {
        DateFormat formatter = new SimpleDateFormat(DatabaseContract.DATE_FORMAT_STRING, Locale.US);
        Date parsed = null;
        try {
            parsed = formatter.parse(transactionDate);
        } catch (ParseException e) {
            Log.e(TAG, "String could not be parsed to a Date object");
            e.printStackTrace();
        }
        return transactionDate; // TODO date return format
//        return parsed != null ? DateFormat.getDateInstance().format(parsed) : transactionDate;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getValue() {
        return value;
    }

    public String getCategory() {
        return category;
    }

    public TransactionListItem(int id,
                               String transactionDate,
                               int transactionType,
                               String description,
                               String accountName,
                               double value,
                               String category) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.description = description;
        this.accountName = accountName;
        this.value = value;
        this.category = category;
    }

    public static TransactionListItem fromCursor(Cursor cursor) {
        int tmpId;
        String tmpTDate;
        int tmpTType;
        String tmpDescription;
        String tmpAccountName;
        double tmpValue;
        String tmpCategory;
        if (cursor != null) {
            tmpId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TransactionEntry._ID));
            tmpTDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.TransactionEntry.COL_DATE));
            tmpTType = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TransactionEntry.COL_TYPE));
            tmpDescription = cursor.getString(cursor.getColumnIndex(DatabaseContract.TransactionEntry.COL_DESCRIPTION));
            tmpAccountName = cursor.getString(cursor.getColumnIndex(DatabaseContract.AccountEntry.COL_NAME));
            tmpValue = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.TransactionEntry.COL_VALUE));
            tmpCategory = cursor.getString(cursor.getColumnIndex(DatabaseContract.CategoryEntry.COL_CATEGORY));
            return new TransactionListItem(tmpId, tmpTDate, tmpTType, tmpDescription, tmpAccountName, tmpValue, tmpCategory);
        } else {
            return null;
        }
    }

    public static boolean validate() {
        //TODO
        return true;
    }
}
