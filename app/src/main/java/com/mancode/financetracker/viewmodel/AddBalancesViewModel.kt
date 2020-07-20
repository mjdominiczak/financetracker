package com.mancode.financetracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.pojos.AccountNameCurrency
import com.mancode.financetracker.database.pojos.BalanceExtended
import com.mancode.financetracker.repository.AccountsRepository
import com.mancode.financetracker.repository.BalancesRepository
import org.threeten.bp.LocalDate

class AddBalancesViewModel(
        accountsRepository: AccountsRepository,
        private val balancesRepository: BalancesRepository,
        val date: LocalDate
) : ViewModel() {

    val accounts: LiveData<List<AccountNameCurrency>> = accountsRepository.getAccountsActiveOn(date)
    val balances: LiveData<List<BalanceExtended>> = balancesRepository.getBalancesForDate(date)

    fun addEditBalance(balance: BalanceEntity) {
        if (balance.id == 0) {
            balancesRepository.insertBalance(balance)
        } else {
            balancesRepository.updateBalance(balance)
        }
    }

    fun removeBalance(balance: BalanceEntity) {
        balancesRepository.removeBalance(balance)
    }
}