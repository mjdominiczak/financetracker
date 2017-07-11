package com.mancode.financetracker;

import android.provider.BaseColumns;

/**
 * Created by Manveru on 07.05.2017.
 */

public final class DatabaseContract {

    private static final String TEXT_TYPE   = " TEXT";
    private static final String REAL_TYPE   = " REAL";
    private static final String INT_TYPE    = " INTEGER";

    private DatabaseContract() {}

    public static abstract class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME               = "accounts";
        public static final String COLUMN_NAME_NAME         = "name";
        public static final String COLUMN_NAME_TYPE         = "type";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE %s" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s," +
                        "%s %s)",
                TABLE_NAME,
                _ID,
                COLUMN_NAME_NAME, TEXT_TYPE,
                COLUMN_NAME_TYPE, TEXT_TYPE);
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class BalanceEntry implements BaseColumns {
        public static final String TABLE_NAME               = "balances";
        public static final String COLUMN_NAME_CHECK_DATE   = "check_date";
        public static final String COLUMN_NAME_ACCOUNT      = "account";
        public static final String COLUMN_NAME_BALANCE      = "balance";
        public static final String COLUMN_NAME_CURRENCY     = "currency";
        public static final String COLUMN_NAME_FIXED        = "fixed";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s'" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s," +
                        "%s %s REFERENCES %s(%s)," +
                        "%s %s," +
                        "%s %s REFERENCES %s(%s)," +
                        "%s %s)",
                TABLE_NAME,
                _ID,
                COLUMN_NAME_CHECK_DATE, TEXT_TYPE,
                COLUMN_NAME_ACCOUNT, INT_TYPE, AccountEntry.TABLE_NAME, AccountEntry._ID,
                COLUMN_NAME_BALANCE, REAL_TYPE,
                COLUMN_NAME_CURRENCY, INT_TYPE, CurrencyEntry.TABLE_NAME, CurrencyEntry._ID,
                COLUMN_NAME_FIXED, TEXT_TYPE);
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class TransactionEntry implements BaseColumns {
        public static final String TABLE_NAME               = "transactions";
        public static final String COLUMN_NAME_DATE         = "date";
        public static final String COLUMN_NAME_TYPE         = "type";
        public static final String COLUMN_NAME_DESCRIPTION  = "description";
        public static final String COLUMN_NAME_VALUE        = "value";
        public static final String COLUMN_NAME_CURRENCY     = "currency";
        public static final String COLUMN_NAME_ACCOUNT      = "account";
        public static final String COLUMN_NAME_CATEGORY     = "category";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s'" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s," +
                        "%s %s," +
                        "%s %s," +
                        "%s %s," +
                        "%s %s REFERENCES %s(%s)," +
                        "%s %s REFERENCES %s(%s)," +
                        "%s %s REFERENCES %s(%s))",
                TABLE_NAME,
                _ID,
                COLUMN_NAME_DATE, TEXT_TYPE,
                COLUMN_NAME_TYPE, TEXT_TYPE,
                COLUMN_NAME_DESCRIPTION, TEXT_TYPE,
                COLUMN_NAME_VALUE, REAL_TYPE,
                COLUMN_NAME_CURRENCY, INT_TYPE, CurrencyEntry.TABLE_NAME, CurrencyEntry.COLUMN_NAME_CURRENCY,
                COLUMN_NAME_ACCOUNT, INT_TYPE, AccountEntry.TABLE_NAME, AccountEntry.COLUMN_NAME_NAME,
                COLUMN_NAME_CATEGORY, INT_TYPE, CategoryEntry.TABLE_NAME, CategoryEntry.COLUMN_NAME_CATEGORY);
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class CurrencyEntry implements BaseColumns {
        public static final String TABLE_NAME               = "currencies";
        public static final String COLUMN_NAME_CURRENCY     = "currency";
        public static final String COLUMN_NAME_EXCHANGE_RATE= "exchange_rate";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s'" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s," +
                        "%s %s)",
                TABLE_NAME,
                _ID,
                COLUMN_NAME_CURRENCY, TEXT_TYPE,
                COLUMN_NAME_EXCHANGE_RATE, TEXT_TYPE);
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME               = "categories";
        public static final String COLUMN_NAME_CATEGORY     = "category";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s'" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s)",
                TABLE_NAME,
                _ID,
                COLUMN_NAME_CATEGORY, TEXT_TYPE);
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}