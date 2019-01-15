package com.mancode.financetracker.database.entity;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

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

    public static final int TYPE_INCOME = 1;
    public static final int TYPE_OUTCOME = -1;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "transaction_date")
    public Date date;

    @ColumnInfo(name = "transaction_type")
    public int type;

    @ColumnInfo(name = "transaction_description")
    public String description;

    @ColumnInfo(name = "transaction_value")
    public double value;

    @ColumnInfo(name = "transaction_account")
    public int accountId;

    @ColumnInfo(name = "transaction_category")
    public int categoryId;

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
}
