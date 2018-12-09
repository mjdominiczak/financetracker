package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.entity.BalanceExtended;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface BalanceDao {

    @Query("SELECT * FROM balances")
    LiveData<List<BalanceEntity>> getAllBalancesLive();

    @Query("SELECT * FROM balances")
    List<BalanceEntity> getAllBalances();

    // TODO queried columns to be optimized
    @Query("SELECT * FROM balances INNER JOIN accounts ON balance_account_id = accounts._id " +
            "ORDER BY date(balance_check_date) DESC, balance_account_id ASC")
    LiveData<List<BalanceExtended>> getBalancesForDisplay();

    @Query("SELECT * FROM balances WHERE _id = :id")
    BalanceEntity getBalanceById(int id);

    @Insert
    void insertBalance(BalanceEntity balance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BalanceEntity> balanceList);

    @Query("DELETE FROM balances WHERE _id = (SELECT MAX(_id) FROM balances)")
    void removeLast();
}
