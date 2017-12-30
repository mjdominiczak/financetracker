package com.mancode.financetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Manveru on 07.05.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    private static final int    DATABASE_VERSION    = 1;
    static final String         DATABASE_NAME       = "database.db";

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
        db.execSQL(DatabaseContract.BalanceEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.CategoryEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.CurrencyEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }
}
