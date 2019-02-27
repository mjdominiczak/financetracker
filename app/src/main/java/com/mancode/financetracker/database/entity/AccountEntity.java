package com.mancode.financetracker.database.entity;

import com.mancode.financetracker.database.converter.DateConverter;

import org.threeten.bp.LocalDate;

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
    public int id;

    @ColumnInfo(name = "account_name")
    public String accountName;

    @ColumnInfo(name = "account_type")
    public int accountType;

    @ColumnInfo(name = "account_currency")
    public String currency;

    @ColumnInfo(name = "account_open_date")
    public LocalDate openDate;

    @ColumnInfo(name = "account_close_date")
    public LocalDate closeDate;

    public AccountEntity(int id, String accountName,
                         int accountType, String currency,
                         LocalDate openDate, LocalDate closeDate) {
        this.id = id;
        this.accountName = accountName;
        this.accountType = accountType;
        this.currency = currency;
        this.openDate = openDate;
        this.closeDate = closeDate;
    }

    @Override
    public String toString() {
        return accountName;
    }
}
