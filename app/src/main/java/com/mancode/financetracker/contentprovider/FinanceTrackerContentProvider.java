package com.mancode.financetracker.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
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
    private static final int BALANCE = 300;
    private static final int BALANCE_ID = 301;
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
        Cursor returnCursor;
        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                returnCursor = db.query(
                        DatabaseContract.AccountEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ACCOUNT_ID:
                long _id = ContentUris.parseId(uri);
                returnCursor = db.query(
                        DatabaseContract.AccountEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.AccountEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            case TRANSACTION:
                returnCursor = db.query(
                        DatabaseContract.TransactionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRANSACTION_ID:
                _id = ContentUris.parseId(uri);
                returnCursor = db.query(
                        DatabaseContract.TransactionEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.TransactionEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            case BALANCE:
                returnCursor = db.query(
                        DatabaseContract.BalanceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BALANCE_ID:
                _id = ContentUris.parseId(uri);
                returnCursor = db.query(
                        DatabaseContract.BalanceEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.BalanceEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            case CATEGORY:
                returnCursor = db.query(
                        DatabaseContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CATEGORY_ID:
                _id = ContentUris.parseId(uri);
                returnCursor = db.query(
                        DatabaseContract.CategoryEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.CategoryEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            case CURRENCY:
                returnCursor = db.query(
                        DatabaseContract.CurrencyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CURRENCY_ID:
                _id = ContentUris.parseId(uri);
                returnCursor = db.query(
                        DatabaseContract.CurrencyEntry.TABLE_NAME,
                        projection,
                        DatabaseContract.CurrencyEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
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
            case BALANCE:
                return DatabaseContract.BalanceEntry.CONTENT_TYPE;
            case BALANCE_ID:
                return DatabaseContract.BalanceEntry.CONTENT_ITEM_TYPE;
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
                _id = db.insert(DatabaseContract.AccountEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.AccountEntry.buildAccountUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case TRANSACTION:
                _id = db.insert(DatabaseContract.TransactionEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.TransactionEntry.buildTransactionUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case BALANCE:
                _id = db.insert(DatabaseContract.BalanceEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.BalanceEntry.buildBalanceUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case CATEGORY:
                _id = db.insert(DatabaseContract.CategoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DatabaseContract.CategoryEntry.buildCategoryUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case CURRENCY:
                _id = db.insert(DatabaseContract.CurrencyEntry.TABLE_NAME, null, values);
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
                rows = db.delete(DatabaseContract.AccountEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRANSACTION:
                rows = db.delete(DatabaseContract.TransactionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BALANCE:
                rows = db.delete(DatabaseContract.BalanceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rows = db.delete(DatabaseContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CURRENCY:
                rows = db.delete(DatabaseContract.CurrencyEntry.TABLE_NAME, selection, selectionArgs);
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
                rows = db.update(DatabaseContract.AccountEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRANSACTION:
                rows = db.update(DatabaseContract.TransactionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BALANCE:
                rows = db.update(DatabaseContract.BalanceEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CATEGORY:
                rows = db.update(DatabaseContract.CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CURRENCY:
                rows = db.update(DatabaseContract.CurrencyEntry.TABLE_NAME, values, selection, selectionArgs);
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
        matcher.addURI(content, DatabaseContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(content, DatabaseContract.PATH_CATEGORY + "/#", CATEGORY_ID);
        matcher.addURI(content, DatabaseContract.PATH_CURRENCY, CURRENCY);
        matcher.addURI(content, DatabaseContract.PATH_CURRENCY + "/#", CURRENCY_ID);
        matcher.addURI(content, DatabaseContract.PATH_TRANSACTION, TRANSACTION);
        matcher.addURI(content, DatabaseContract.PATH_TRANSACTION + "/#", TRANSACTION_ID);

        return matcher;
    }

}
