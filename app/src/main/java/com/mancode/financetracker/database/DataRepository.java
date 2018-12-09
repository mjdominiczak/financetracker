package com.mancode.financetracker.database;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mancode.financetracker.database.dao.AccountDao;
import com.mancode.financetracker.database.dao.BalanceDao;
import com.mancode.financetracker.database.dao.CategoryDao;
import com.mancode.financetracker.database.dao.TransactionDao;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.entity.BalanceExtended;
import com.mancode.financetracker.database.entity.CategoryEntity;
import com.mancode.financetracker.database.entity.TransactionEntity;

import java.util.List;

/**
 * Created by Manveru on 02.02.2018.
 */

public class DataRepository {

    private static DataRepository sInstance;

    private final FTDatabase mDatabase;

    private AccountDao mAccountDao;
    private BalanceDao mBalanceDao;
    private CategoryDao mCategoryDao;
    private TransactionDao mTransactionDao;

    private LiveData<List<AccountEntity>> mAllAccounts;
    private LiveData<List<BalanceExtended>> mAllBalances;
    private LiveData<List<CategoryEntity>> mAllCategories;
    private LiveData<List<TransactionEntity>> mAllTransactions;

    private DataRepository(Application application) {
        mDatabase = FTDatabase.getInstance(application);
        mAccountDao = mDatabase.accountDao();
        mAllAccounts = mAccountDao.getAllAccountsLive();
        mBalanceDao = mDatabase.balanceDao();
        mAllBalances = mBalanceDao.getBalancesForDisplay();
        mCategoryDao = mDatabase.categoryDao();
        mAllCategories = mCategoryDao.getAllCategoriesLive();
        mTransactionDao = mDatabase.transactionDao();
        mAllTransactions = mTransactionDao.getAllTransactionsLive();
    }

    public static DataRepository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(application);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<AccountEntity>> getAllAccounts() {
        return mAllAccounts;
    }

    public LiveData<List<BalanceExtended>> getAllBalances() {
        return mAllBalances;
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return mAllCategories;
    }

    public LiveData<List<TransactionEntity>> getAllTransactions() {
        return mAllTransactions;
    }

    public void insertAccount(AccountEntity account) {
        AsyncTask.execute(() -> mAccountDao.insertAccount(account));
    }

    public void insertBalance(BalanceEntity balance) {
        AsyncTask.execute(() -> mBalanceDao.insertBalance(balance));
    }

    public void insertTransaction(TransactionEntity transaction) {
        AsyncTask.execute(() -> mTransactionDao.insertTransaction(transaction));
    }

    public void removeLastBalance() {
        AsyncTask.execute(() -> mBalanceDao.removeLast());
    }
}
