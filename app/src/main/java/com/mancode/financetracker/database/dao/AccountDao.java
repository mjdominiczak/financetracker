package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mancode.financetracker.database.entity.AccountEntity;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts")
    LiveData<List<AccountEntity>> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE _id = :id")
    AccountEntity getAccountById(int id);

    @Insert
    void insertAccount(AccountEntity account);
}
