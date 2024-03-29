package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.pojos.BalanceExtended;

import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface BalanceDao {

    @Query("SELECT * FROM balances")
    List<BalanceEntity> getAllBalancesSimple();

    @Query("SELECT * FROM balances WHERE balance_fixed = 1")
    List<BalanceEntity> getAllFixedBalancesSimple();

    @Query("SELECT * FROM balances INNER JOIN accounts ON balance_account_id = accounts._id " +
            "WHERE balance_check_date = " +
            "(SELECT balance_check_date FROM balances ORDER BY balance_check_date DESC LIMIT 1) " +
            "AND account_type = 1 " +
            "ORDER BY balance_account_id ASC")
    LiveData<List<BalanceEntity>> getActualAssets();

    @Query("SELECT * FROM balances INNER JOIN accounts ON balance_account_id = accounts._id " +
            "WHERE balance_check_date = " +
            "(SELECT balance_check_date FROM balances ORDER BY balance_check_date DESC LIMIT 1) " +
            "AND account_type = -1 " +
            "ORDER BY balance_account_id ASC")
    LiveData<List<BalanceEntity>> getActualLiabilities();

    @Query("SELECT balances._id, balance_check_date, balance_value, balance_account_id, " +
            "balance_fixed, account_name, account_type, account_currency " +
            "FROM balances INNER JOIN accounts ON balance_account_id = accounts._id " +
            "WHERE balance_check_date = :date ORDER BY balance_account_id ASC")
    LiveData<List<BalanceExtended>> getFullBalancesForDate(LocalDate date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBalance(BalanceEntity balance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BalanceEntity> balanceList);

    @Update
    void updateBalance(BalanceEntity balance);

    @Delete
    void removeBalance(BalanceEntity balance);

    @Query("DELETE FROM balances WHERE balance_fixed = 0")
    void clearNotFixed();
}
