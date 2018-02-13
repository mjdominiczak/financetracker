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

@Entity(tableName = "transactions",
        foreignKeys = {
                @ForeignKey(
                        entity = AccountEntity.class,
                        parentColumns = "_id",
                        childColumns = "transaction_account"),
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = "_id",
                        childColumns = "transaction_category")
        }
)
@TypeConverters(DateConverter.class)
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "transaction_date")
    private Date date;

    @ColumnInfo(name = "transaction_type")
    private int type;

    @ColumnInfo(name = "transaction_description")
    private String description;

    @ColumnInfo(name = "transaction_value")
    private double value;

    @ColumnInfo(name = "transaction_account")
    private int accountId;

    @ColumnInfo(name = "transaction_category")
    private int categoryId;

    public TransactionEntity(int id, Date date, int type, String description,
                             double value, int accountId, int categoryId) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.description = description;
        this.value = value;
        this.accountId = accountId;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
