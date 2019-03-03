package com.mancode.financetracker.database.entity;

import com.mancode.financetracker.database.converter.DateConverter;

import org.threeten.bp.LocalDate;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "net_values")
public class NetValue {

    @NonNull
    @PrimaryKey
    @TypeConverters(DateConverter.class)
    private LocalDate date;

    private double value;

    private boolean calculated;

    private boolean complete;

    public NetValue(@NonNull LocalDate date, double value, boolean calculated) {
        this.date = date;
        this.value = value;
        this.calculated = calculated;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @NonNull
    public LocalDate getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public boolean isComplete() {
        return complete;
    }

}
