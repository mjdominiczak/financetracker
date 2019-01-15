package com.mancode.financetracker.database.pojos;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

public class BalanceMini {

    @ColumnInfo(name = "balance_check_date")
    @TypeConverters(DateConverter.class)
    public Date checkDate;

    @ColumnInfo(name = "balance_value")
    public double value;

    @ColumnInfo(name = "account_type")
    public int accountType;

    @ColumnInfo(name = "account_currency")
    public String AccountCurrency;
}
