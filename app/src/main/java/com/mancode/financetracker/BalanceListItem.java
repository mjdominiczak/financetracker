package com.mancode.financetracker;

import android.database.Cursor;

import com.mancode.financetracker.database.DatabaseContract;

/**
 * Created by Manveru on 06.09.2017.
 */

public class BalanceListItem {

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
        return checkDate;
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
            tmpCheckDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COLUMN_NAME_CHECK_DATE));
            tmpAccount = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COLUMN_NAME_ACCOUNT));
            tmpBalance = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COLUMN_NAME_BALANCE));
            tmpCurrency = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COLUMN_NAME_CURRENCY));
            tmpFixed = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COLUMN_NAME_FIXED));
            return new BalanceListItem(tmpId, tmpCheckDate, tmpAccount, tmpBalance, tmpCurrency, tmpFixed);
        } else {
            return null;
        }
    }

}
