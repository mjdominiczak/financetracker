package com.mancode.financetracker.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mancode.financetracker.database.DatabaseContract;
import com.mancode.financetracker.database.DatabaseHelper;

/**
 * Created by Manveru on 16.08.2017.
 */

public class FinanceTrackerContentProvider extends ContentProvider {

    private static final int ACCOUNT = 100;
    private static final int ACCOUNT_ID = 101;
    private static final int TRANSACTION = 200;
    private static final int TRANSACTION_ID = 201;
    private static final int TRANSACTION_JOINED = 250;
    private static final int TRANSACTION_JOINED_ID = 251;
    private static final int BALANCE = 300;
    private static final int BALANCE_ID = 301;
    private static final int BALANCE_JOINED = 350;
    private static final int BALANCE_JOINED_ID = 351;
    private static final int CATEGORY = 400;
    private static final int CATEGORY_ID = 401;
    private static final int CURRENCY = 500;
    private static final int CURRENCY_ID = 501;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = DatabaseHelper.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                builder.setTables(DatabaseContract.AccountEntry.TBL_NAME);
                break;
            case ACCOUNT_ID:
                builder.setTables(DatabaseContract.AccountEntry.TBL_NAME);
                builder.appendWhere(
                        DatabaseContract.AccountEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case TRANSACTION:
                builder.setTables(DatabaseContract.TransactionEntry.TBL_NAME);
                break;
            case TRANSACTION_ID:
                builder.setTables(DatabaseContract.TransactionEntry.TBL_NAME);
                builder.appendWhere(
                        DatabaseContract.TransactionEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case TRANSACTION_JOINED:
                builder.setTables(DatabaseContract.TransactionEntryJoined.INNER_JOIN_STATEMENT);
                useAuthorityUri = true;
                break;
            case TRANSACTION_JOINED_ID:
                builder.setTables(DatabaseContract.TransactionEntryJoined.INNER_JOIN_STATEMENT);
                builder.appendWhere(
                        DatabaseContract.TransactionEntryJoined._ID + " = " + uri.getLastPathSegment());
                useAuthorityUri = true;
                break;
            case BALANCE:
                builder.setTables(DatabaseContract.BalanceEntry.TBL_NAME);
                break;
            case BALANCE_ID:
                builder.setTables(DatabaseContract.BalanceEntry.TBL_NAME);
                builder.appendWhere(
                        DatabaseContract.BalanceEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case BALANCE_JOINED:
                builder.setTables(DatabaseContract.BalanceEntryJoined.INNER_JOIN_STATEMENT);
                useAuthorityUri = true;
                break;
            case BALANCE_JOINED_ID:
                builder.setTables(DatabaseContract.BalanceEntryJoined.INNER_JOIN_STATEMENT);
                builder.appendWhere(
                        DatabaseContract.BalanceEntryJoined._ID + " = " + uri.getLastPathSegment());
                useAuthorityUri = true;
                break;
            case CATEGORY:
                builder.setTables(DatabaseContract.CategoryEntry.TBL_NAME);
                break;
            case CATEGORY_ID:
                builder.setTables(DatabaseContract.CategoryEntry.TBL_NAME);
                builder.appendWhere(
                        DatabaseContract.CategoryEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case CURRENCY:
                builder.setTables(DatabaseContract.CurrencyEntry.TBL_NAME);
                break;
            case CURRENCY_ID:
                builder.setTables(DatabaseContract.CurrencyEntry.TBL_NAME);
                builder.appendWhere(
                        DatabaseContract.CurrencyEntry._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        returnCursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        if (useAuthorityUri) {
            returnCursor.setNotificationUri(getContext().getContentResolver(), DatabaseContract.BASE_CONTENT_URI);
        } else {
            returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                return DatabaseContract.AccountEntry.CONTENT_TYPE;
            case ACCOUNT_ID:
                return DatabaseContract.AccountEntry.CONTENT_ITEM_TYPE;
            case TRANSACTION:
                return DatabaseContract.TransactionEntry.CONTENT_TYPE;
            case TRANSACTION_ID:
                return DatabaseContract.TransactionEntry.CONTENT_ITEM_TYPE;
            case TRANSACTION_JOINED:
                return DatabaseContract.TransactionEntryJoined.CONTENT_TYPE;
            case TRANSACTION_JOINED_ID:
                return DatabaseContract.TransactionEntryJoined.CONTENT_ITEM_TYPE;
            case BALANCE:
                return DatabaseContract.BalanceEntry.CONTENT_TYPE;
            case BALANCE_ID:
                return DatabaseContract.BalanceEntry.CONTENT_ITEM_TYPE;
            case BALANCE_JOINED:
                return DatabaseContract.BalanceEntryJoined.CONTENT_TYPE;
            case BALANCE_JOINED_ID:
                return DatabaseContract.BalanceEntryJoined.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return DatabaseContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return DatabaseContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case CURRENCY:
                return DatabaseContract.CurrencyEntry.CONTENT_TYPE;
            case CURRENCY_ID:
                return DatabaseContract.CurrencyEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long _id;
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                _id = db.insert(DatabaseContract.AccountEntry.TBL_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.AccountEntry.buildAccountUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case TRANSACTION:
                _id = db.insert(DatabaseContract.TransactionEntry.TBL_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.TransactionEntry.buildTransactionUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case BALANCE:
                _id = db.insert(DatabaseContract.BalanceEntry.TBL_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.BalanceEntry.buildBalanceUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case CATEGORY:
                _id = db.insert(DatabaseContract.CategoryEntry.TBL_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.CategoryEntry.buildCategoryUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case CURRENCY:
                _id = db.insert(DatabaseContract.CurrencyEntry.TBL_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.CurrencyEntry.buildCurrencyUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rows;
        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                rows = db.delete(DatabaseContract.AccountEntry.TBL_NAME, selection, selectionArgs);
                break;
            case TRANSACTION:
                rows = db.delete(DatabaseContract.TransactionEntry.TBL_NAME, selection, selectionArgs);
                break;
            case BALANCE:
                rows = db.delete(DatabaseContract.BalanceEntry.TBL_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rows = db.delete(DatabaseContract.CategoryEntry.TBL_NAME, selection, selectionArgs);
                break;
            case CURRENCY:
                rows = db.delete(DatabaseContract.CurrencyEntry.TBL_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (selection == null || rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rows;
        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                rows = db.update(DatabaseContract.AccountEntry.TBL_NAME, values, selection, selectionArgs);
                break;
            case TRANSACTION:
                rows = db.update(DatabaseContract.TransactionEntry.TBL_NAME, values, selection, selectionArgs);
                break;
            case BALANCE:
                rows = db.update(DatabaseContract.BalanceEntry.TBL_NAME, values, selection, selectionArgs);
                break;
            case CATEGORY:
                rows = db.update(DatabaseContract.CategoryEntry.TBL_NAME, values, selection, selectionArgs);
                break;
            case CURRENCY:
                rows = db.update(DatabaseContract.CurrencyEntry.TBL_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    private static UriMatcher buildUriMatcher() {
        String content = DatabaseContract.CONTENT_AUTHORITY;
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, DatabaseContract.PATH_ACCOUNT, ACCOUNT);
        matcher.addURI(content, DatabaseContract.PATH_ACCOUNT + "/#", ACCOUNT_ID);
        matcher.addURI(content, DatabaseContract.PATH_BALANCE, BALANCE);
        matcher.addURI(content, DatabaseContract.PATH_BALANCE + "/#", BALANCE_ID);
        matcher.addURI(content, DatabaseContract.PATH_BALANCE_JOINED, BALANCE_JOINED);
        matcher.addURI(content, DatabaseContract.PATH_BALANCE_JOINED + "/#", BALANCE_JOINED_ID);
        matcher.addURI(content, DatabaseContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(content, DatabaseContract.PATH_CATEGORY + "/#", CATEGORY_ID);
        matcher.addURI(content, DatabaseContract.PATH_CURRENCY, CURRENCY);
        matcher.addURI(content, DatabaseContract.PATH_CURRENCY + "/#", CURRENCY_ID);
        matcher.addURI(content, DatabaseContract.PATH_TRANSACTION, TRANSACTION);
        matcher.addURI(content, DatabaseContract.PATH_TRANSACTION + "/#", TRANSACTION_ID);
        matcher.addURI(content, DatabaseContract.PATH_TRANSACTION_JOINED, TRANSACTION_JOINED);
        matcher.addURI(content, DatabaseContract.PATH_TRANSACTION_JOINED + "/#", TRANSACTION_JOINED_ID);

        return matcher;
    }

}
