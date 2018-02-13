package com.mancode.financetracker.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.mancode.financetracker.database.DataRepository;
import com.mancode.financetracker.database.entity.AccountEntity;

import java.util.List;

/**
 * Created by Manveru on 02.02.2018.
 */

public class AccountViewModel extends AndroidViewModel {

    private DataRepository mRepository;
    private LiveData<List<AccountEntity>> mAllAccounts;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        mRepository = DataRepository.getInstance(application);
        mAllAccounts = mRepository.getAllAccounts();
    }

    public LiveData<List<AccountEntity>> getAllAccounts() {
        return mAllAccounts;
    }

    public void insert(AccountEntity account) {
        mRepository.insertAccount(account);
    }
}
