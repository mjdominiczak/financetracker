package com.mancode.financetracker.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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
