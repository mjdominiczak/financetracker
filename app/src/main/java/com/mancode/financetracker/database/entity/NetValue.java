package com.mancode.financetracker.database.entity;

import com.mancode.financetracker.database.converter.DateConverter;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "net_values")
public class NetValue {

    @NonNull
    @PrimaryKey
    @TypeConverters(DateConverter.class)
    private Date date;

    private double value;

    @ColumnInfo(name = "complete")
    private boolean isComplete;

    public NetValue(@NonNull Date date, double value) {
        this.date = date;
        this.value = value;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }

    public boolean isComplete() {
        return isComplete;
    }

}
