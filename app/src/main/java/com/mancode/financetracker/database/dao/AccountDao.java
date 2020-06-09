package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.pojos.AccountMini;
import com.mancode.financetracker.database.pojos.AccountNameCurrency;
import com.mancode.financetracker.database.views.AccountExtended;

import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface AccountDao {

    @Query("SELECT * FROM accounts_view")
    LiveData<List<AccountExtended>> getAllAccountsLive();

    @Query("SELECT * FROM accounts")
    LiveData<List<AccountEntity>> getAll();

    @Query("SELECT * FROM accounts")
    List<AccountEntity> getAllAccounts();

    @Query("SELECT _id, account_name FROM accounts")
    List<AccountMini> getAccountsNamesAndIds();

    @Query("SELECT * FROM accounts WHERE _id = :id")
    LiveData<AccountEntity> getAccountById(int id);

    @Query("SELECT _id, account_name, account_currency FROM accounts " +
            "WHERE account_open_date <= :date")
    LiveData<List<AccountNameCurrency>> getAccountsActiveOn(LocalDate date);

    @Query("SELECT COUNT(*) FROM accounts WHERE account_type = :type")
    LiveData<Integer> getActiveAccountsOfType(int type);

    @Insert
    void insertAccount(AccountEntity account);

    @Update
    void update(AccountEntity account);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AccountEntity> accountList);
}
