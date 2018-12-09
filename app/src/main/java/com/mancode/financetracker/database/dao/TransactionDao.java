package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mancode.financetracker.database.entity.TransactionEntity;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date(transaction_date) DESC, _id DESC")
    LiveData<List<TransactionEntity>> getAllTransactionsLive();

    @Query("SELECT * FROM transactions")
    List<TransactionEntity> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE _id = :id")
    TransactionEntity getTransactionById(int id);

    @Insert
    void insertTransaction(TransactionEntity transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TransactionEntity> transactionList);
}
