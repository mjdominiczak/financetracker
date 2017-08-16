package com.mancode.financetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        db.execSQL(DatabaseContract.BalanceEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.CategoryEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.CurrencyEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearDB(db);
        onCreate(db);
    }

    public long addAccount(String name, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_NAME, name);
            cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_TYPE, type);
            long result = db.insert(DatabaseContract.AccountEntry.TABLE_NAME, null, cv);
            if (result != -1) {
                int id = (int)result;
                new AccountItem(id, name, type);
            }
            return result;
        } else {
            return -1;
        }
    }

    private void clearDB(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.AccountEntry.DELETE_TABLE);
        db.execSQL(DatabaseContract.TransactionEntry.DELETE_TABLE);
        AccountItem.ITEMS.clear();
        AccountItem.ITEM_MAP.clear();
    }

    public void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        clearDB(db);
        onCreate(db);
    }

    public int deleteAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            int result = db.delete(
                    DatabaseContract.AccountEntry.TABLE_NAME,
                    DatabaseContract.AccountEntry._ID + " = ?s",
                    new String[]{Integer.toString(id)});
            if (result != -1) {
                AccountItem.ITEMS.remove(AccountItem.ITEM_MAP.get(id));
                AccountItem.ITEM_MAP.remove(id);
            }
            return result;
        } else return -1;
    }

    public List<AccountItem> getAllAccountsFromDB() {
        if (AccountItem.ITEMS.isEmpty()) {
            SQLiteDatabase db = this.getReadableDatabase();
            if (db != null) {
                Cursor result = db.rawQuery("SELECT * FROM " + DatabaseContract.AccountEntry.TABLE_NAME, null);
                result.moveToFirst();
                while (!result.isAfterLast()) {
                    new AccountItem(
                            result.getInt(result.getColumnIndex(DatabaseContract.AccountEntry._ID)),
                            result.getString(result.getColumnIndex(DatabaseContract.AccountEntry.COLUMN_NAME_NAME)),
                            result.getString(result.getColumnIndex(DatabaseContract.AccountEntry.COLUMN_NAME_TYPE))
                    );
                    result.moveToNext();
                }
                result.close();
            }
        }
        return AccountItem.ITEMS;
    }

    public static class AccountItem {

        public static final List<AccountItem> ITEMS = new ArrayList<>();
        public static final Map<Integer, AccountItem> ITEM_MAP = new HashMap<>();

        public final int id;
        public final String name;
        public final String type;

        public AccountItem(int id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
            ITEMS.add(this);
            ITEM_MAP.put(id, this);
        }

        @Override
        public String toString() {
            return name + " - " + type;
        }
    }
}
