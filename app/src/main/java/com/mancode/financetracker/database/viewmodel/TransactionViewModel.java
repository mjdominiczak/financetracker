package com.mancode.financetracker.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.mancode.financetracker.database.DataRepository;
import com.mancode.financetracker.database.entity.TransactionEntity;

import java.util.List;

/**
 * Created by Manveru on 12.02.2018.
 */

public class TransactionViewModel extends AndroidViewModel {

    private DataRepository mRepository;
    private LiveData<List<TransactionEntity>> mAllTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        mRepository = DataRepository.getInstance(application);
        mAllTransactions = mRepository.getAllTransactions();
    }

    public LiveData<List<TransactionEntity>> getAllTransactions() {
        return mAllTransactions;
    }

    public void insertTransaction(TransactionEntity transaction) {
        mRepository.insertTransaction(transaction);
    }
}
