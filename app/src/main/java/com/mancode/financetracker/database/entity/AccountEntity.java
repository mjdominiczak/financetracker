package com.mancode.financetracker.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

/**
 * Created by Manveru on 23.01.2018.
 */

@Entity(tableName = "accounts",
        foreignKeys = @ForeignKey(entity = CurrencyEntity.class,
                                  parentColumns = "_id",
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
    private int currency;

    @ColumnInfo(name = "account_open_date")
    private Date openDate;

    @ColumnInfo(name = "account_close_date")
    private Date closeDate;

    public AccountEntity(int id, String accountName,
                         int accountType, int currency,
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

    public int getCurrency() {
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
