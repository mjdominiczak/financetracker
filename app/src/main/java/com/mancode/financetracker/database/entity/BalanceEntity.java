package com.mancode.financetracker.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

/**
 * Created by Manveru on 25.01.2018.
 */

@Entity(tableName = "balances",
        foreignKeys = @ForeignKey(entity = AccountEntity.class,
                parentColumns = "_id",
                childColumns = "balance_account_id"))
@TypeConverters(DateConverter.class)
public class BalanceEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "balance_check_date")
    private Date checkDate;

    @ColumnInfo(name = "balance_account_id")
    private int accountId;

    @ColumnInfo(name = "balance_value")
    private double value;

    @ColumnInfo(name = "balance_fixed")
    private boolean fixed;

    public BalanceEntity(int id, Date checkDate, int accountId, double value, boolean fixed) {
        this.id = id;
        this.checkDate = checkDate;
        this.accountId = accountId;
        this.value = value;
        this.fixed = fixed;
    }

    public int getId() {
        return id;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public int getAccountId() {
        return accountId;
    }

    public double getValue() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }
}
