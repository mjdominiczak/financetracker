package com.mancode.financetracker.database.viewmodel;

import android.app.Application;

import com.mancode.financetracker.database.DataRepository;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.views.AccountExtended;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * Created by Manveru on 02.02.2018.
 */

public class AccountViewModel extends AndroidViewModel {

    private DataRepository mRepository;
    private LiveData<List<AccountExtended>> mAllAccounts;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        mRepository = DataRepository.getInstance(application);
        mAllAccounts = mRepository.getAllAccounts();
    }

    public LiveData<List<AccountExtended>> getAllAccounts() {
        return mAllAccounts;
    }

    public void insert(AccountEntity account) {
        mRepository.insertAccount(account);
    }
}
