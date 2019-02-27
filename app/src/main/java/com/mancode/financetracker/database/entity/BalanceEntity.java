package com.mancode.financetracker.database.entity;

import com.mancode.financetracker.database.converter.DateConverter;

import org.threeten.bp.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

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
    private LocalDate checkDate;

    @ColumnInfo(name = "balance_account_id")
    private int accountId;

    @ColumnInfo(name = "balance_value")
    private double value;

    @ColumnInfo(name = "balance_fixed")
    private boolean fixed;

    public BalanceEntity(int id, LocalDate checkDate, int accountId, double value, boolean fixed) {
        this.id = id;
        this.checkDate = checkDate;
        this.accountId = accountId;
        this.value = value;
        this.fixed = fixed;
    }

    public int getId() {
        return id;
    }

    public LocalDate getCheckDate() {
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
