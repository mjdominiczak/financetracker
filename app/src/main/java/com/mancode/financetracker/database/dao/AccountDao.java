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
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.pojos.AccountNameCurrency;
import com.mancode.financetracker.database.views.AccountExtended;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface AccountDao {

    @Query("SELECT * FROM accounts_view")
    LiveData<List<AccountExtended>> getAllAccountsExt();

    @Query("SELECT * FROM accounts")
    LiveData<List<AccountEntity>> getAll();

    @Query("SELECT * FROM accounts")
    List<AccountEntity> getAllAccountsSimple();

    @Query("SELECT * FROM accounts WHERE _id = :id")
    LiveData<AccountEntity> getAccountById(int id);

    @Query("SELECT _id, account_name, account_currency FROM accounts " +
            "WHERE account_open_date <= :date AND " +
            "(account_close_date IS NULL OR account_close_date >= :date)")
    LiveData<List<AccountNameCurrency>> getAccountsActiveOn(LocalDate date);

    @Query("SELECT COUNT(*) FROM accounts WHERE account_type = :type AND " +
            "account_open_date <= DATE('now', 'localtime') AND " +
            "(account_close_date IS NULL OR account_close_date >= DATE('now', 'localtime'))")
    LiveData<Integer> getActiveAccountsOfType(int type);

    @Query("SELECT transaction_date FROM transactions WHERE transaction_account = :accountId " +
            "ORDER BY transaction_date DESC LIMIT 1")
    LocalDate getLastTransactionDate(int accountId);

    @Query("SELECT balance_check_date FROM balances WHERE balance_account_id = :accountId " +
            "ORDER BY balance_check_date DESC LIMIT 1")
    LocalDate getLastBalanceDate(int accountId);

    @Insert
    void insertAccount(AccountEntity account);

    @Update
    void update(AccountEntity account);

    @Delete
    void remove(AccountEntity account);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AccountEntity> accountList);
}
