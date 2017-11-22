package com.mancode.financetracker;

import android.database.Cursor;
import android.util.Log;

import com.mancode.financetracker.database.DatabaseContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Manveru on 06.09.2017.
 */

public class BalanceListItem {

    private static final String TAG = BalanceListItem.class.getSimpleName();

    private int id;
    private final String checkDate;
    private final String account;
    private final int accountType;
    private final double balance;
    private final boolean fixed;

    public int getId() {
        return id;
    }

    public String getCheckDate() {
        DateFormat formatter = new SimpleDateFormat(DatabaseContract.DATE_FORMAT_STRING, Locale.US);
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

    public int getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isFixed() {
        return fixed;
    }

    public BalanceListItem(int id, String checkDate, String account, int accountType, double balance, boolean fixed) {
        this.id = id;
        this.checkDate = checkDate;
        this.account = account;
        this.accountType = accountType;
        this.balance = balance;
        this.fixed = fixed;
    }

    public static BalanceListItem fromCursor(Cursor cursor) {
        int tmpId;
        String tmpAccount;
        String tmpCheckDate;
        int tmpAccountType;
        double tmpBalance;
        boolean tmpFixed;
        if (cursor != null) {
            tmpId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.BalanceEntry._ID));
            tmpCheckDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_CHECK_DATE));
            tmpAccount = cursor.getString(cursor.getColumnIndex(DatabaseContract.AccountEntry.COL_NAME));
            tmpAccountType = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AccountEntry.COL_TYPE));
            tmpBalance = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_BALANCE));
            tmpFixed = cursor.getInt(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_FIXED)) != 0;
            return new BalanceListItem(tmpId, tmpCheckDate, tmpAccount, tmpAccountType, tmpBalance, tmpFixed);
        } else {
            return null;
        }
    }

    static double calculateDailyBalance(List<BalanceListItem> itemList) {
        double result = 0.0;
        for (BalanceListItem item : itemList) {
            result += ((double) item.getAccountType()) * item.getBalance();
        }
        return result;
    }

    public static boolean validate(String checkDate, int account, double balance) {
        return !checkDate.isEmpty() && account >= 0;
    }
}
