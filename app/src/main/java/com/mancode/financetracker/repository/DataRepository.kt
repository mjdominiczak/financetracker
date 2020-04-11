package com.mancode.financetracker.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.dao.*
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.CurrencyEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.AccountMini
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
    val allTransactions: LiveData<List<TransactionFull>>

    val accountsNamesAndIds: List<AccountMini>
        get() = accountDao.accountsNamesAndIds

    val allCategories: List<CategoryEntity>
        get() = categoryDao.allCategories

    init {
        val database = FTDatabase.getInstance(application)
        accountDao = database.accountDao()
        allAccounts = accountDao.allAccountsLive
        balanceDao = database.balanceDao()
        categoryDao = database.categoryDao()
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

    fun getTransaction(id: Int): LiveData<TransactionEntity> = transactionDao.getTransaction(id)

    fun insertTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.insertTransaction(transaction) }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.updateTransaction(transaction) }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.deleteTransaction(transaction) }
    }

    fun toggleBookmark(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.toggleFlag(transaction.id, TransactionEntity.BOOKMARKED) }
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
