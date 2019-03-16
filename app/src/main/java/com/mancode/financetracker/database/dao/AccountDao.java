package com.mancode.financetracker.database.dao;

import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.views.AccountExtended;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts_view")
    LiveData<List<AccountExtended>> getAllAccountsLive();

    @Query("SELECT * FROM accounts")
    List<AccountEntity> getAllAccounts();

    @Query("SELECT account_name FROM accounts")
    List<String> getAccountsNames();

    @Query("SELECT * FROM accounts WHERE _id = :id")
    AccountEntity getAccountById(int id);

    @Insert
    void insertAccount(AccountEntity account);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AccountEntity> accountList);
}
