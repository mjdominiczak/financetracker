package com.mancode.financetracker.database.dao;

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

    @Query("SELECT COUNT(*) FROM " + CurrencyEntity.TABLE_NAME)
    int count();

    @Query("SELECT * FROM currencies")
    List<CurrencyEntity> getAllCurrencies();

    @Query("SELECT COUNT(*) FROM currencies WHERE currency_symbol = :code")
    int getCurrencyCount(String code);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCurrency(CurrencyEntity currency);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CurrencyEntity> currencyList);
}
