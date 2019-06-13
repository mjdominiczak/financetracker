package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.pojos.BalanceExtended
import com.mancode.financetracker.repository.BalancesRepository

/**
 * Created by Manveru on 03.02.2018.
 */

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val balancesRepository: BalancesRepository = BalancesRepository.getInstance(
            FTDatabase.getInstance(application).netValueDao(),
            FTDatabase.getInstance(application).balanceDao())

    val balances: LiveData<List<BalanceExtended>> = balancesRepository.getBalances()
    val netValues: LiveData<List<NetValue>> = balancesRepository.getKeyedNetValues()

    fun insert(balance: BalanceEntity) {
        balancesRepository.insertBalance(balance)
    }

    fun removeLastBalance() {
        balancesRepository.removeLastBalance()
    }
}
