package com.mancode.financetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mancode.financetracker.repository.AccountsRepository
import com.mancode.financetracker.repository.BalancesRepository
import org.threeten.bp.LocalDate

class AddBalancesViewModelFactory(
        private val accountsRepository: AccountsRepository,
        private val balancesRepository: BalancesRepository,
        private val date: LocalDate
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddBalancesViewModel(accountsRepository, balancesRepository, date) as T
    }
}