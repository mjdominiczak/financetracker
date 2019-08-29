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
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.pojos.TransactionFull;

import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface TransactionDao {

    @Query("SELECT transactions._id, transaction_date, transaction_type, transaction_description, " +
            "transaction_value, transaction_flags, transaction_account, transaction_category," +
            "account_name, account_type, account_currency, account_open_date, account_close_date, " +
            "category, category_type " +
            "FROM transactions LEFT JOIN accounts ON transaction_account = accounts._id " +
            "LEFT JOIN categories ON transaction_category = categories._id " +
            "ORDER BY date(transaction_date) DESC, transactions._id DESC")
    LiveData<List<TransactionFull>> getAllTransactionsLive();

    @Query("SELECT * FROM transactions")
    List<TransactionEntity> getAllTransactions();

    @Query("SELECT transactions._id, transaction_date, transaction_type, transaction_description, " +
            "transaction_value, transaction_flags, transaction_account, transaction_category," +
            "account_name, account_type, account_currency, account_open_date, account_close_date, " +
            "category, category_type " +
            "FROM transactions LEFT JOIN accounts ON transaction_account = accounts._id " +
            "LEFT JOIN categories ON transaction_category = categories._id " +
            "WHERE date(transaction_date) >= date(:from)" +
            "AND date(transaction_date) <= date(:to)")
    LiveData<List<TransactionEntity>> getTransactionsFromRange(LocalDate from, LocalDate to);

    @Query("SELECT * FROM transactions WHERE transaction_date = :date")
    List<TransactionEntity> getTransactionsForDate(LocalDate date);

    @Insert
    void insertTransaction(TransactionEntity transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TransactionEntity> transactionList);

    @Update
    void updateTransaction(TransactionEntity transaction);

    @Delete
    void deleteTransaction(TransactionEntity transaction);
}
