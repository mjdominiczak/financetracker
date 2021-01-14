package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mancode.financetracker.database.entity.CurrencyEntity;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface CurrencyDao {

    @Query("SELECT * FROM currencies")
    List<CurrencyEntity> getAllCurrenciesSimple();

    @Query("SELECT * FROM currencies")
    LiveData<List<CurrencyEntity>> getAllCurrenciesLive();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCurrency(CurrencyEntity currency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CurrencyEntity> currencyList);
}
