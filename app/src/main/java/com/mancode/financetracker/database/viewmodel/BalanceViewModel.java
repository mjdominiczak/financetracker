package com.mancode.financetracker.database.viewmodel;

import android.app.Application;

import com.mancode.financetracker.database.DataRepository;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.pojos.BalanceExtended;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * Created by Manveru on 03.02.2018.
 */

public class BalanceViewModel extends AndroidViewModel {

    private DataRepository mRepository;
    private LiveData<List<BalanceExtended>> mAllBalances;

    public BalanceViewModel(@NonNull Application application) {
        super(application);
        mRepository = DataRepository.getInstance(application);
        mAllBalances = mRepository.getAllBalances();
    }

    public LiveData<List<BalanceExtended>> getAllBalances() {
        return mAllBalances;
    }

    public void insert(BalanceEntity balance) {
        mRepository.insertBalance(balance);
    }

    public void removeLastBalance() {
        mRepository.removeLastBalance();
    }
}
