package com.mancode.financetracker.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mancode.financetracker.database.dao.AccountDao
import com.mancode.financetracker.database.dao.CurrencyDao
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.CurrencyEntity
import com.mancode.financetracker.database.pojos.AccountNameCurrency
import com.mancode.financetracker.database.views.AccountExtended
import org.threeten.bp.LocalDate

class AccountsRepository private constructor(
        private val accountDao: AccountDao,
        private val currencyDao: CurrencyDao) {

    val allAccounts: LiveData<List<AccountEntity>> = accountDao.all
    val allAccountsExt: LiveData<List<AccountExtended>> = accountDao.allAccountsExt

    fun getAccountById(id: Int): LiveData<AccountEntity> = accountDao.getAccountById(id)

    fun getAccountsActiveOn(date: LocalDate): LiveData<List<AccountNameCurrency>> =
            accountDao.getAccountsActiveOn(date)

    fun getAssetsAccountsCount(): LiveData<Int> =
            accountDao.getActiveAccountsOfType(AccountEntity.TYPE_ASSETS)

    fun getLiabilitiesAccountsCount(): LiveData<Int> =
            accountDao.getActiveAccountsOfType(AccountEntity.TYPE_LIABILITIES)

    fun insert(account: AccountEntity) {
        AsyncTask.execute {
            currencyDao.insertCurrency(CurrencyEntity(
                    account.currency,
                    1.0,
                    null
            )) // will be ignored if currency already exists in the db
            accountDao.insertAccount(account)
        }
    }

    fun update(account: AccountEntity) {
        AsyncTask.execute {
            accountDao.update(account)
        }
    }

    fun remove(account: AccountEntity) {
        AsyncTask.execute {
            accountDao.remove(account)
        }
    }

    fun getLastUsageDate(accountId: Int): LiveData<LocalDate?> {
        val retVal = MutableLiveData<LocalDate?>()
        AsyncTask.execute {
            val dateTransaction = accountDao.getLastTransactionDate(accountId)
            val dateBalance = accountDao.getLastBalanceDate(accountId)
            retVal.postValue(when {
                dateTransaction == null -> dateBalance
                dateBalance == null -> dateTransaction
                dateTransaction.isAfter(dateBalance) -> dateTransaction
                else -> dateBalance
            })
        }
        return retVal
    }

    companion object {
        @Volatile
        private var instance: AccountsRepository? = null

        fun getInstance(accountDao: AccountDao, currencyDao: CurrencyDao): AccountsRepository =
                instance ?: synchronized(this) {
                    instance ?: AccountsRepository(accountDao, currencyDao).also { instance = it }
                }
    }

}