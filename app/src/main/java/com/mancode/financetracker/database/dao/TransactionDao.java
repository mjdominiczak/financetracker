package com.mancode.financetracker.database.dao;

import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.entity.TransactionFull;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface TransactionDao {

    @Query("SELECT transactions._id, transaction_date, transaction_type, transaction_description, " +
            "transaction_value, transaction_account, transaction_category, account_name, " +
            "account_type, account_currency, account_open_date, account_close_date, " +
            "category, category_type " +
            "FROM transactions LEFT JOIN accounts ON transaction_account = accounts._id " +
            "LEFT JOIN categories ON transaction_category = categories._id " +
            "ORDER BY date(transaction_date) DESC, transactions._id DESC")
    LiveData<List<TransactionFull>> getAllTransactionsLive();

    @Query("SELECT * FROM transactions")
    List<TransactionEntity> getAllTransactions();

    @Insert
    void insertTransaction(TransactionEntity transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TransactionEntity> transactionList);

    @Delete
    void deleteTransaction(TransactionEntity transaction);
}
