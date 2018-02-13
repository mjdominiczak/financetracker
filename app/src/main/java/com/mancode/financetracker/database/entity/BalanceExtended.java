package com.mancode.financetracker.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.TypeConverters;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

/**
 * Created by Manveru on 03.02.2018.
 */

public class BalanceExtended {

    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "balance_check_date")
    @TypeConverters(DateConverter.class)
    private Date checkDate;

    @ColumnInfo(name = "balance_value")
    private double value;

    @ColumnInfo(name = "account_name")
    private String accountName;

    @ColumnInfo(name = "account_type")
    private int accountType;

    public BalanceExtended(int id, Date checkDate,
                           double value, String accountName,
                           int accountType) {
        this.id = id;
        this.checkDate = checkDate;
        this.value = value;
        this.accountName = accountName;
        this.accountType = accountType;
    }

    public int getId() {
        return id;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public double getValue() {
        return value;
    }

    public String getAccountName() {
        return accountName;
    }

    public int getAccountType() {
        return accountType;
    }
}
