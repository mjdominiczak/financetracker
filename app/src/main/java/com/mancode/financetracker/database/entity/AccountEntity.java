package com.mancode.financetracker.database.entity;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

/**
 * Created by Manveru on 23.01.2018.
 */

@Entity(tableName = "accounts",
        foreignKeys = @ForeignKey(entity = CurrencyEntity.class,
                                  parentColumns = "currency_symbol",
                                  childColumns = "account_currency"))
@TypeConverters(DateConverter.class)
public class AccountEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "account_name")
    private String accountName;

    @ColumnInfo(name = "account_type")
    private int accountType;

    @ColumnInfo(name = "account_currency")
    private String currency;

    @ColumnInfo(name = "account_open_date")
    private Date openDate;

    @ColumnInfo(name = "account_close_date")
    private Date closeDate;

    public AccountEntity(int id, String accountName,
                         int accountType, String currency,
                         Date openDate, Date closeDate) {
        this.id = id;
        this.accountName = accountName;
        this.accountType = accountType;
        this.currency = currency;
        this.openDate = openDate;
        this.closeDate = closeDate;
    }

    public int getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    @Override
    public String toString() {
        return accountName;
    }
}
