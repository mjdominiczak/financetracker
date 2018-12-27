package com.mancode.financetracker.database.views;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.TypeConverters;

@DatabaseView(viewName = "accounts_view",
        value = "SELECT accounts._id, account_name, account_type, balance_check_date, balance_value, " +
        "(SELECT balances._id FROM balances WHERE balance_account_id = accounts._id " +
        "ORDER BY date(balance_check_date) DESC LIMIT 1) AS balance_id " +
        "FROM accounts LEFT JOIN balances ON balance_id = balances._id")
@TypeConverters(DateConverter.class)
public class AccountExtended {

    @ColumnInfo(name = "_id")
    public int id;
    @ColumnInfo(name = "account_name")
    public String accountName;
    @ColumnInfo(name = "account_type")
    public int accountType;
    @ColumnInfo(name = "balance_check_date")
    public Date balanceCheckDate;
    @ColumnInfo(name = "balance_value")
    public double balanceValue;
    @ColumnInfo(name = "balance_id")
    public int balanceId;

    @NonNull
    @Override
    public String toString() {
        return accountName;
    }
}
