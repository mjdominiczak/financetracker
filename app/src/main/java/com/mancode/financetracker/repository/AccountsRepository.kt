package com.mancode.financetracker.repository

import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.AccountDao
import com.mancode.financetracker.database.pojos.AccountNameCurrency
import org.threeten.bp.LocalDate

class AccountsRepository private constructor(
        private val accountDao: AccountDao) {

    fun getAccountsActiveOn(date: LocalDate): LiveData<List<AccountNameCurrency>> =
            accountDao.getAccountsActiveOn(date)

    companion object {
        @Volatile
        private var instance: AccountsRepository? = null

        fun getInstance(accountDao: AccountDao): AccountsRepository =
                instance ?: synchronized(this) {
                    instance ?: AccountsRepository(accountDao).also { instance = it }
                }
    }

}