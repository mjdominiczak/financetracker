package com.mancode.financetracker.database.pojos;

import com.mancode.financetracker.database.converter.DateConverter;

import org.threeten.bp.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

/**
 * Created by Manveru on 03.02.2018.
 */

public class BalanceExtended {

    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "balance_check_date")
    @TypeConverters(DateConverter.class)
    public LocalDate checkDate;

    @ColumnInfo(name = "balance_value")
    public double value;

    @ColumnInfo(name = "account_name")
    public String accountName;

    @ColumnInfo(name = "account_type")
    public int accountType;

    @ColumnInfo(name = "account_currency")
    public String accountCurrency;

    public BalanceExtended(int id, LocalDate checkDate,
                           double value, String accountName,
                           int accountType, String accountCurrency) {
        this.id = id;
        this.checkDate = checkDate;
        this.value = value;
        this.accountName = accountName;
        this.accountType = accountType;
        this.accountCurrency = accountCurrency;
    }
}
