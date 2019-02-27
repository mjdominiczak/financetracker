package com.mancode.financetracker.database.dao;

import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.NetValue;

import org.threeten.bp.LocalDate;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

@Dao
@TypeConverters(DateConverter.class)
public interface NetValueDao {

    @Query("SELECT * FROM net_values")
    LiveData<List<NetValue>> getValues();

    @Query("SELECT * FROM net_values WHERE date <= :date ORDER BY date DESC LIMIT 1")
    LiveData<NetValue> getValueBefore(LocalDate date);

    @Query("SELECT * FROM net_values WHERE date >= :date ORDER BY date ASC LIMIT 1")
    LiveData<NetValue> getValueAfter(LocalDate date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NetValue> netValues);
}
