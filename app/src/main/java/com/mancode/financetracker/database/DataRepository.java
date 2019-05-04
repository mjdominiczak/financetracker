package com.mancode.financetracker.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.mancode.financetracker.database.dao.AccountDao;
import com.mancode.financetracker.database.dao.BalanceDao;
import com.mancode.financetracker.database.dao.CategoryDao;
import com.mancode.financetracker.database.dao.CurrencyDao;
import com.mancode.financetracker.database.dao.TransactionDao;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.entity.CategoryEntity;
import com.mancode.financetracker.database.entity.CurrencyEntity;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.pojos.AccountMini;
import com.mancode.financetracker.database.pojos.BalanceExtended;
import com.mancode.financetracker.database.pojos.TransactionFull;
import com.mancode.financetracker.database.views.AccountExtended;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Manveru on 02.02.2018.
 */

public class DataRepository {

    private static DataRepository sInstance;

    private AccountDao mAccountDao;
    private BalanceDao mBalanceDao;
    private CategoryDao mCategoryDao;
    private CurrencyDao mCurrencyDao;
    private TransactionDao mTransactionDao;

    private LiveData<List<AccountExtended>> mAllAccounts;
    private LiveData<List<BalanceExtended>> mAllBalances;
    private LiveData<List<CategoryEntity>> mAllCategories;
    private LiveData<List<TransactionFull>> mAllTransactions;

    private DataRepository(Application application) {
        FTDatabase database = FTDatabase.getInstance(application);
        mAccountDao = database.accountDao();
        mAllAccounts = mAccountDao.getAllAccountsLive();
        mBalanceDao = database.balanceDao();
        mAllBalances = mBalanceDao.getBalancesForDisplay();
        mCategoryDao = database.categoryDao();
        mAllCategories = mCategoryDao.getAllCategoriesLive();
        mCurrencyDao = database.currencyDao();
        mTransactionDao = database.transactionDao();
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

    public LiveData<List<AccountExtended>> getAllAccounts() {
        return mAllAccounts;
    }

    public List<AccountMini> getAccountsNamesAndIds() {
        return mAccountDao.getAccountsNamesAndIds();
    }

    public LiveData<List<BalanceExtended>> getAllBalances() {
        return mAllBalances;
    }

    public LiveData<List<CategoryEntity>> getAllCategories() {
        return mAllCategories;
    }

    public List<CategoryEntity> getIncomeCategories() {
        return mCategoryDao.getIncomeCategoriesLive();
    }

    public List<CategoryEntity> getOutcomeCategories() {
        return mCategoryDao.getOutcomeCategoriesLive();
    }

    public LiveData<List<TransactionFull>> getAllTransactions() {
        return mAllTransactions;
    }

    public void insertAccount(AccountEntity account) {
        AsyncTask.execute(() -> {
            if (mCurrencyDao.getCurrencyCount(account.currency) == 0) {
                mCurrencyDao.insertCurrency(new CurrencyEntity(
                        0,
                        account.currency,
                        1.0
                ));
            }
            mAccountDao.insertAccount(account);
        });
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

    public void updateTransaction(@NotNull TransactionEntity transaction) {
        AsyncTask.execute(() -> mTransactionDao.updateTransaction(transaction));
    }

    public void deleteTransaction(TransactionEntity transaction) {
        AsyncTask.execute(() -> mTransactionDao.deleteTransaction(transaction));
    }
}
