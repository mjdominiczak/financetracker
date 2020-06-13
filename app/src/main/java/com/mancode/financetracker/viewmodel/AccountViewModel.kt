package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.views.AccountExtended
import com.mancode.financetracker.repository.AccountsRepository
import com.mancode.financetracker.utils.InjectorUtils

/**
 * Created by Manveru on 02.02.2018.
 */
class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AccountsRepository = InjectorUtils.getAccountsRepository(application)

    val allAccounts: LiveData<List<AccountExtended>> = repository.allAccountsExt

    fun insert(account: AccountEntity) {
        repository.insert(account)
    }
}