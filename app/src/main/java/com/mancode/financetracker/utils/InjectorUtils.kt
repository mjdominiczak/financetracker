package com.mancode.financetracker.utils

import android.content.Context
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.repository.AccountsRepository
import com.mancode.financetracker.repository.BalancesRepository
import com.mancode.financetracker.repository.NetValuesRepository
import com.mancode.financetracker.repository.TransactionsRepository
import com.mancode.financetracker.viewmodel.AddBalancesViewModelFactory
import org.threeten.bp.LocalDate

object InjectorUtils {

    fun getAccountsRepository(context: Context): AccountsRepository {
        return AccountsRepository.getInstance(FTDatabase.getInstance(context).accountDao())
    }

    private fun getBalancesRepository(context: Context): BalancesRepository {
        val db = FTDatabase.getInstance(context)
        return BalancesRepository.getInstance(db.netValueDao(), db.balanceDao())
    }

    fun getNetValuesRepository(context: Context): NetValuesRepository {
        return NetValuesRepository.getInstance(FTDatabase.getInstance(context).netValueDao())
    }

    fun getTransactionsRepository(context: Context): TransactionsRepository {
        return TransactionsRepository.getInstance(FTDatabase.getInstance(context).transactionDao())
    }

    fun provideAddBalancesViewModelFactory(
            context: Context,
            date: LocalDate
    ): AddBalancesViewModelFactory {
        return AddBalancesViewModelFactory(
                getAccountsRepository(context),
                getBalancesRepository(context),
                date)
    }

}