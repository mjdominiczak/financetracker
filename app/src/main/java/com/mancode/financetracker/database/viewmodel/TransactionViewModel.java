package com.mancode.financetracker.database.viewmodel;

import android.app.Application;

import com.mancode.financetracker.database.DataRepository;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.entity.TransactionFull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * Created by Manveru on 12.02.2018.
 */

public class TransactionViewModel extends AndroidViewModel {

    private DataRepository mRepository;
    private LiveData<List<TransactionFull>> mAllTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        mRepository = DataRepository.getInstance(application);
        mAllTransactions = mRepository.getAllTransactions();
    }

    public LiveData<List<TransactionFull>> getAllTransactions() {
        return mAllTransactions;
    }

    public void insertTransaction(TransactionEntity transaction) {
        mRepository.insertTransaction(transaction);
    }

    public void deleteTransaction(TransactionEntity transaction) {
        mRepository.deleteTransaction(transaction);
    }
}
