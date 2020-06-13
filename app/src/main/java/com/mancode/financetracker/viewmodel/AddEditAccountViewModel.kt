package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.repository.AccountsRepository
import com.mancode.financetracker.utils.InjectorUtils
import org.threeten.bp.LocalDate

class AddEditAccountViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AccountsRepository by lazy {
        InjectorUtils.getAccountsRepository(application)
    }

    fun getAccount(id: Int): LiveData<AccountEntity> = repository.getAccountById(id)

    fun getLastUsageDate(id: Int): LiveData<LocalDate?> = repository.getLastUsageDate(id)

    fun insert(account: AccountEntity) = repository.insert(account)
    fun update(account: AccountEntity) = repository.update(account)
    fun remove(account: AccountEntity) = repository.remove(account)
}