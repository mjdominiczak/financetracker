package com.mancode.financetracker.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.*
import com.mancode.financetracker.database.entity.*
import com.mancode.financetracker.database.pojos.AccountMini
import com.mancode.financetracker.database.pojos.BalanceExtended
import com.mancode.financetracker.database.pojos.TransactionFull
import com.mancode.financetracker.database.views.AccountExtended

/**
 * Created by Manveru on 02.02.2018.
 */

class DataRepository private constructor(application: Application) {

    private val accountDao: AccountDao
    private val balanceDao: BalanceDao
    private val categoryDao: CategoryDao
    private val currencyDao: CurrencyDao
    private val transactionDao: TransactionDao

    val allAccounts: LiveData<List<AccountExtended>>
    val allBalances: LiveData<List<BalanceExtended>>
    val allCategories: LiveData<List<CategoryEntity>>
    val allTransactions: LiveData<List<TransactionFull>>

    val accountsNamesAndIds: List<AccountMini>
        get() = accountDao.accountsNamesAndIds

    val incomeCategories: List<CategoryEntity>
        get() = categoryDao.incomeCategoriesLive

    val outcomeCategories: List<CategoryEntity>
        get() = categoryDao.outcomeCategoriesLive

    init {
        val database = FTDatabase.getInstance(application)
        accountDao = database.accountDao()
        allAccounts = accountDao.allAccountsLive
        balanceDao = database.balanceDao()
        allBalances = balanceDao.balancesForDisplay
        categoryDao = database.categoryDao()
        allCategories = categoryDao.allCategoriesLive
        currencyDao = database.currencyDao()
        transactionDao = database.transactionDao()
        allTransactions = transactionDao.allTransactionsLive
    }

    fun insertAccount(account: AccountEntity) {
        AsyncTask.execute {
            currencyDao.insertCurrency(CurrencyEntity(
                    account.currency,
                    1.0,
                    null
            ))
            accountDao.insertAccount(account)
        }
    }

    fun insertCurrencies(currencies: List<CurrencyEntity>) {
        AsyncTask.execute { currencyDao.insertAll(currencies) }
    }

    fun insertBalance(balance: BalanceEntity) {
        AsyncTask.execute { balanceDao.insertBalance(balance) }
    }

    fun insertTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.insertTransaction(transaction) }
    }

    fun removeLastBalance() {
        AsyncTask.execute { balanceDao.removeLast() }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.updateTransaction(transaction) }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.deleteTransaction(transaction) }
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(application: Application): DataRepository =
                instance ?: synchronized(this) {
                    instance ?: DataRepository(application).also { instance = it }
                }
    }
}
