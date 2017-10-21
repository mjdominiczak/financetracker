package com.mancode.financetracker.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Manveru on 07.05.2017.
 */

public final class DatabaseContract {

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    public static final String CONTENT_AUTHORITY = "com.mancode.financetracker.database";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACCOUNT         = "account";
    public static final String PATH_BALANCE         = "balance";
    public static final String PATH_BALANCE_JOINED  = "balance_joined";
    public static final String PATH_CATEGORY        = "category";
    public static final String PATH_CURRENCY        = "currency";
    public static final String PATH_TRANSACTION     = "transaction";

    private static final String TEXT_TYPE   = " TEXT";
    private static final String REAL_TYPE   = " REAL";
    private static final String INT_TYPE    = " INTEGER";

    private DatabaseContract() {}

    public static abstract class AccountEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI + "/" + PATH_ACCOUNT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI + "/" + PATH_ACCOUNT;

        public static final String TBL_NAME         = "accounts";
        public static final String COL_NAME         = "account_name";
        public static final String COL_TYPE         = "account_type";
        public static final String COL_CURRENCY_ID  = "account_currency";
        public static final String COL_OPEN_DATE    = "account_open_date";
        public static final String COL_CLOSE_DATE   = "account_close_date";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE %s" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s," +
                        "%s %s," +
                        "%s %s REFERENCES %s(%s)," +
                        "%s %s," +
                        "%s %s)",
                TBL_NAME,
                _ID,
                COL_NAME, TEXT_TYPE,
                COL_TYPE, TEXT_TYPE,
                COL_CURRENCY_ID, INT_TYPE, CurrencyEntry.TBL_NAME, CurrencyEntry._ID,
                COL_OPEN_DATE, TEXT_TYPE,
                COL_CLOSE_DATE, TEXT_TYPE);
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;

        public static Uri buildAccountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static abstract class BalanceEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BALANCE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI + "/" + PATH_BALANCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI + "/" + PATH_BALANCE;

        public static final String TBL_NAME = "balances";
        public static final String COL_CHECK_DATE = "balance_check_date";
        public static final String COL_ACCOUNT_ID = "balance_account_id";
        public static final String COL_BALANCE = "balance_value";
        public static final String COL_FIXED = "balance_fixed";
        public static final String DEFAULT_FIXED = "1";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s'" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s," +
                        "%s %s REFERENCES %s(%s)," +
                        "%s %s," +
                        "%s %s DEFAULT " + DEFAULT_FIXED + ")",
                TBL_NAME,
                _ID,
                COL_CHECK_DATE, TEXT_TYPE,
                COL_ACCOUNT_ID, INT_TYPE, AccountEntry.TBL_NAME, AccountEntry._ID,
                COL_BALANCE, REAL_TYPE,
                COL_FIXED, INT_TYPE);
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;

        public static Uri buildBalanceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static abstract class BalanceEntryJoined implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BALANCE_JOINED).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI + "/" + PATH_BALANCE_JOINED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI + "/" + PATH_BALANCE_JOINED;

        public static final String INNER_JOIN_STATEMENT =
                BalanceEntry.TBL_NAME + " INNER JOIN " + AccountEntry.TBL_NAME +
                        " ON (" + BalanceEntry.COL_ACCOUNT_ID +
                        "=" + AccountEntry.TBL_NAME + "." + AccountEntry._ID + ")";
    }

    public static abstract class TransactionEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTION).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI + "/" + PATH_TRANSACTION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI + "/" + PATH_TRANSACTION;

        public static final String TBL_NAME = "transactions";
        public static final String COL_DATE = "transaction_date";
        public static final String COL_TYPE = "transaction_type";
        public static final String COL_DESCRIPTION = "transaction_description";
        public static final String COL_VALUE = "transaction_value";
        public static final String COL_CURRENCY_ID = "transaction_currency";
        public static final String COL_ACCOUNT_ID = "transaction_account";
        public static final String COL_CATEGORY_ID = "transaction_category";

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
                TBL_NAME,
                _ID,
                COL_DATE, TEXT_TYPE,
                COL_TYPE, TEXT_TYPE,
                COL_DESCRIPTION, TEXT_TYPE,
                COL_VALUE, REAL_TYPE,
                COL_CURRENCY_ID, INT_TYPE, CurrencyEntry.TBL_NAME, CurrencyEntry.COL_CURRENCY,
                COL_ACCOUNT_ID, INT_TYPE, AccountEntry.TBL_NAME, AccountEntry.COL_NAME,
                COL_CATEGORY_ID, INT_TYPE, CategoryEntry.TBL_NAME, CategoryEntry.COL_CATEGORY);
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;

        public static Uri buildTransactionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static abstract class CurrencyEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENCY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI + "/" + PATH_CURRENCY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI + "/" + PATH_CURRENCY;

        public static final String TBL_NAME = "currencies";
        public static final String COL_CURRENCY = "currency_symbol";
        public static final String COL_EXCHANGE_RATE = "currency_exchange_rate";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s'" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s," +
                        "%s %s)",
                TBL_NAME,
                _ID,
                COL_CURRENCY, TEXT_TYPE,
                COL_EXCHANGE_RATE, TEXT_TYPE);
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;

        public static Uri buildCurrencyUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static abstract class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + CONTENT_URI + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + CONTENT_URI + "/" + PATH_CATEGORY;

        public static final String TBL_NAME = "categories";
        public static final String COL_CATEGORY = "category";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s'" +
                        "(%s INTEGER PRIMARY KEY," +
                        "%s %s)",
                TBL_NAME,
                _ID,
                COL_CATEGORY, TEXT_TYPE);
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}