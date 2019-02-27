package com.mancode.financetracker.database.pojos;

import com.mancode.financetracker.database.converter.DateConverter;

import org.threeten.bp.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

public class BalanceMini {

    @ColumnInfo(name = "balance_check_date")
    @TypeConverters(DateConverter.class)
    public LocalDate checkDate;

    @ColumnInfo(name = "balance_value")
    public double value;

    @ColumnInfo(name = "account_type")
    public int accountType;

    @ColumnInfo(name = "account_currency")
    public String AccountCurrency;
}
