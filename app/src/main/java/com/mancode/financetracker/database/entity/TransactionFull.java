package com.mancode.financetracker.database.entity;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.TypeConverters;

@TypeConverters(DateConverter.class)
public class TransactionFull {

    @Embedded
    public TransactionEntity transaction;
    @ColumnInfo(name = "account_name")
    public String accountName;
    @ColumnInfo(name = "account_type")
    public int accountType;
    @ColumnInfo(name = "account_currency")
    public String currency;
    @ColumnInfo(name = "account_open_date")
    public Date openDate;
    @ColumnInfo(name = "account_close_date")
    public Date closeDate;
    @ColumnInfo(name = "category")
    public String category;
    @ColumnInfo(name = "category_type")
    public int categoryType;

    public TransactionFull(TransactionEntity transaction,
                           String accountName,
                           int accountType,
                           String currency,
                           Date openDate,
                           Date closeDate,
                           String category,
                           int categoryType) {
        this.transaction = transaction;
        this.accountName = accountName;
        this.accountType = accountType;
        this.currency = currency;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.category = category;
        this.categoryType = categoryType;
    }
}
