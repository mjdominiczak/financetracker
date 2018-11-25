package com.mancode.financetracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Manveru on 23.01.2018.
 */

@Entity(tableName = CurrencyEntity.TABLE_NAME)
public class CurrencyEntity {

    public static final String TABLE_NAME = "currencies";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "currency_symbol")
    private String currencySymbol;

    @ColumnInfo(name = "currency_exchange_rate")
    private double exchangeRate;

    @Ignore
    public CurrencyEntity() {

    }

    public CurrencyEntity(int id, String currencySymbol, double exchangeRate) {
        initFromValues(id, currencySymbol, exchangeRate);
    }

    public void initFromValues(int id, String currencySymbol, double exchangeRate) {
        this.id = id;
        this.currencySymbol = currencySymbol;
        this.exchangeRate = exchangeRate;

    }

    public int getId() {
        return id;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }
}
