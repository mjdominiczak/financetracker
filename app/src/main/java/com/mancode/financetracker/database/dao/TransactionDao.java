package com.mancode.financetracker.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mancode.financetracker.database.entity.TransactionEntity;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date(transaction_date) DESC, _id DESC")
    LiveData<List<TransactionEntity>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE _id = :id")
    TransactionEntity getTransactionById(int id);

    @Insert
    void insertTransaction(TransactionEntity transaction);
}
