package com.mancode.financetracker.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.DataRepository
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.pojos.BalanceExtended

/**
 * Created by Manveru on 03.02.2018.
 */

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = DataRepository.getInstance(application)

    val allBalances: LiveData<List<BalanceExtended>> = repository.allBalances

    fun insert(balance: BalanceEntity) {
        repository.insertBalance(balance)
    }

    fun removeLastBalance() {
        repository.removeLastBalance()
    }
}
