package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.views.AccountExtended
import com.mancode.financetracker.repository.DataRepository
import com.mancode.financetracker.repository.DataRepository.Companion.getInstance

/**
 * Created by Manveru on 02.02.2018.
 */
class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: DataRepository
    val allAccounts: LiveData<List<AccountExtended>>

    fun insert(account: AccountEntity?) {
        mRepository.insertAccount(account!!)
    }

    init {
        mRepository = getInstance(application)
        allAccounts = mRepository.allAccounts
    }
}