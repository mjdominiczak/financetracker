package com.mancode.financetracker.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Manveru on 23.01.2018.
 */

@Entity(tableName = "currencies")
public class CurrencyEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int mId;

    @ColumnInfo(name = "currency_symbol")
    private String mCurrencySymbol;

    @ColumnInfo(name = "currency_exchange_rate")
    private double mExchangeRate;

    public CurrencyEntity(int id, String currencySymbol, double exchangeRate) {
        this.mId = id;
        this.mCurrencySymbol = currencySymbol;
        this.mExchangeRate = exchangeRate;
    }

    public int getId() {
        return mId;
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    public double getExchangeRate() {
        return mExchangeRate;
    }
}
