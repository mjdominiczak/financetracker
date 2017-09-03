package com.mancode.financetracker;

import android.database.Cursor;

import com.mancode.financetracker.database.DatabaseContract;

/**
 * Created by Manveru on 22.08.2017.
 */

public class AccountListItem {

    public AccountListItem(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    private int id;
    private String name;
    private String type;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static AccountListItem fromCursor(Cursor cursor) {
        int tmpId;
        String tmpName;
        String tmpType;
        if (cursor != null) {
            tmpId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AccountEntry._ID));
            tmpName = cursor.getString(cursor.getColumnIndex(DatabaseContract.AccountEntry.COLUMN_NAME_NAME));
            tmpType = cursor.getString(cursor.getColumnIndex(DatabaseContract.AccountEntry.COLUMN_NAME_TYPE));
            return new AccountListItem(tmpId, tmpName, tmpType);
        } else {
            return null;
        }
    }
}
