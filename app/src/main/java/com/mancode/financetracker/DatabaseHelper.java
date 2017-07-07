package com.mancode.financetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Manveru on 07.05.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    private static final int     DATABASE_VERSION    = 1;
    private static final String  DATABASE_NAME       = "database.db";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
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
            return db.insert(DatabaseContract.AccountEntry.TABLE_NAME, null, cv);
        } else {
            return -1;
        }
    }

    public int deleteAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            return db.delete(
                    DatabaseContract.AccountEntry.TABLE_NAME,
                    DatabaseContract.AccountEntry._ID + " = ?s",
                    new String[]{Integer.toString(id)});
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
