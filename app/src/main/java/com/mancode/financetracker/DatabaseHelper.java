package com.mancode.financetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Manveru on 07.05.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int     DATABASE_VERSION    = 1;
    public static final String  DATA_PATH           = Environment.getExternalStorageDirectory().getPath() + File.separator + "FinanceTrackerData" + File.separator;
    public static final String  DATABASE_NAME       = "database.db";

    public DatabaseHelper(Context context) {
//        super(context, DATA_PATH + DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.AccountEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.TransactionEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.AccountEntry.DELETE_TABLE);
        db.execSQL(DatabaseContract.TransactionEntry.DELETE_TABLE);
        onCreate(db);
    }

    public long addAccount(String description, String type, double balance, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_NAME, description);
            cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_TYPE, type);
            cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_BALANCE, balance);
            cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_CURRENCY, currency);
            return db.insert(DatabaseContract.AccountEntry.TABLE_NAME, null, cv);
        } else {
            return -1;
        }
    }

    public int deleteAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            return db.delete(DatabaseContract.AccountEntry.TABLE_NAME, DatabaseContract.AccountEntry._ID + " = ?s", new String[]{Integer.toString(id)});
        } else return -1;
    }

    public ArrayList<String> getAllAccounts() {
        ArrayList<String> accountsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            Cursor result = db.rawQuery("SELECT * FROM " + DatabaseContract.AccountEntry.TABLE_NAME, null);
            result.moveToFirst();
            while (!result.isAfterLast()) {
                accountsList.add(result.getString(result.getColumnIndex(DatabaseContract.AccountEntry.COLUMN_NAME_NAME)));
                result.moveToNext();
            }
            result.close();
            return accountsList;
        } else {
            return null;
        }
    }
}
