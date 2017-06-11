package com.mancode.financetracker;

import android.provider.BaseColumns;

/**
 * Created by Manveru on 07.05.2017.
 */

public final class DatabaseContract {

    private static final String TEXT_TYPE   = " TEXT";
    private static final String REAL_TYPE   = " REAL";

    private DatabaseContract() {}

    public static abstract class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME               = "account";
        public static final String COLUMN_NAME_NAME         = "name";
        public static final String COLUMN_NAME_TYPE         = "type";
        public static final String COLUMN_NAME_BALANCE      = "balance";
        public static final String COLUMN_NAME_CURRENCY     = "currency";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s %s, %s %s, %s %s, %s %s)",
                TABLE_NAME,
                _ID,
                COLUMN_NAME_NAME, TEXT_TYPE,
                COLUMN_NAME_TYPE, TEXT_TYPE,
                COLUMN_NAME_BALANCE, REAL_TYPE,
                COLUMN_NAME_CURRENCY, TEXT_TYPE);
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class TransactionEntry implements BaseColumns {
        public static final String TABLE_NAME               = "transaction";
        public static final String COLUMN_NAME_DATE         = "date";
        public static final String COLUMN_NAME_TYPE         = "type";
        public static final String COLUMN_NAME_DESCRIPTION  = "description";
        public static final String COLUMN_NAME_VALUE        = "value";
        public static final String COLUMN_NAME_CURRENCY     = "currency";
        public static final String COLUMN_NAME_ACCOUNT      = "account";
        public static final String COLUMN_NAME_CATEGORY     = "category";

        public static final String CREATE_TABLE = String.format(
                "CREATE TABLE '%s' (%s INTEGER PRIMARY KEY, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s, %s %s)",
                TABLE_NAME,
                _ID,
                COLUMN_NAME_DATE, TEXT_TYPE,
                COLUMN_NAME_TYPE, TEXT_TYPE,
                COLUMN_NAME_DESCRIPTION, TEXT_TYPE,
                COLUMN_NAME_VALUE, REAL_TYPE,
                COLUMN_NAME_CURRENCY, TEXT_TYPE,
                COLUMN_NAME_ACCOUNT, TEXT_TYPE,
                COLUMN_NAME_CATEGORY, TEXT_TYPE);
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}