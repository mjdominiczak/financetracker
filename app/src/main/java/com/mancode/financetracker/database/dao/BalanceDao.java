package com.mancode.financetracker.database.dao;

import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.entity.BalanceExtended;
import com.mancode.financetracker.database.pojos.BalanceMini;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface BalanceDao {

    @Query("SELECT * FROM balances")
    LiveData<List<BalanceEntity>> getAllBalancesLive();

    @Query("SELECT * FROM balances")
    List<BalanceEntity> getAllBalances();

    @Query("SELECT balances._id, balance_check_date, balance_value, account_name, account_type, " +
            "account_currency " +
            "FROM balances INNER JOIN accounts ON balance_account_id = accounts._id " +
            "ORDER BY date(balance_check_date) DESC, balance_account_id ASC")
    LiveData<List<BalanceExtended>> getBalancesForDisplay();

    @Query("SELECT DISTINCT balance_check_date FROM balances")
    List<Date> getDateKeys(); // TODO add ordering for performance???

    @Query("SELECT balance_check_date, balance_value, account_type, account_currency " +
            "FROM balances INNER JOIN accounts ON balance_account_id = accounts._id " +
            "WHERE balance_check_date = :date")
    List<BalanceMini> getBalancesForDate(Date date);

    @Insert
    void insertBalance(BalanceEntity balance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BalanceEntity> balanceList);

    @Query("DELETE FROM balances WHERE _id = (SELECT MAX(_id) FROM balances)")
    void removeLast();
}
