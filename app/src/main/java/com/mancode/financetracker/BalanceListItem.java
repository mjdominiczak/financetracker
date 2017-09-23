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
 * Created by Manveru on 06.09.2017.
 */

public class BalanceListItem {

    private static final String TAG = BalanceListItem.class.getSimpleName();

    private int id;
    private final String checkDate;
    private final String account;
    private final double balance;
    private final String currency;
    private final String fixed;

    public int getId() {
        return id;
    }

    public String getCheckDate() {
        DateFormat formatter = new SimpleDateFormat(DatabaseContract.dateFormatString, Locale.US);
        Date parsed = null;
        try {
            parsed = formatter.parse(checkDate);
        } catch (ParseException e) {
            Log.e(TAG, "String could not be parsed to a Date object");
            e.printStackTrace();
        }
        return parsed != null ? DateFormat.getDateInstance().format(parsed) : checkDate;
    }

    public String getAccount() {
        return account;
    }

    public double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFixed() {
        return fixed;
    }

    public BalanceListItem(int id, String checkDate, String account, double balance, String currency, String fixed) {
        this.id = id;
        this.checkDate = checkDate;
        this.account = account;
        this.balance = balance;
        this.currency = currency;
        this.fixed = fixed;
    }

    public static BalanceListItem fromCursor(Cursor cursor) {
        int tmpId;
        String tmpAccount;
        String tmpCheckDate;
        double tmpBalance;
        String tmpCurrency;
        String tmpFixed;
        if (cursor != null) {
            tmpId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.BalanceEntry._ID));
            tmpCheckDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_CHECK_DATE));
            tmpAccount = cursor.getString(cursor.getColumnIndex(DatabaseContract.AccountEntry.COL_NAME));
            tmpBalance = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_BALANCE));
            tmpCurrency = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_CURRENCY_ID));
            tmpFixed = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_FIXED));
            return new BalanceListItem(tmpId, tmpCheckDate, tmpAccount, tmpBalance, tmpCurrency, tmpFixed);
        } else {
            return null;
        }
    }

    public static boolean validate(String checkDate, int account, double balance, String currency, String fixed) {
        return !checkDate.isEmpty() && account >= 0;
    }
}
