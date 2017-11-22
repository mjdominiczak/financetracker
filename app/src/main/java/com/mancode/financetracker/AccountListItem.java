package com.mancode.financetracker;

import android.database.Cursor;

import com.mancode.financetracker.database.DatabaseContract;

/**
 * Created by Manveru on 22.08.2017.
 */

public class AccountListItem {

    public AccountListItem(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    private int id;
    private String name;
    private int type;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public static AccountListItem fromCursor(Cursor cursor) {
        int tmpId;
        String tmpName;
        int tmpType;
        if (cursor != null) {
            tmpId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AccountEntry._ID));
            tmpName = cursor.getString(cursor.getColumnIndex(DatabaseContract.AccountEntry.COL_NAME));
            tmpType = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AccountEntry.COL_TYPE));
            return new AccountListItem(tmpId, tmpName, tmpType);
        } else {
            return null;
        }
    }

    public static boolean validate(String name, int type) {
        return !name.isEmpty() && (type == -1 || type == 1);
    }
}
