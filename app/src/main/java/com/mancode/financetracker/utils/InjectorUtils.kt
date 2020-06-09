package com.mancode.financetracker.utils

import android.content.Context
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.repository.*
import com.mancode.financetracker.viewmodel.AddBalancesViewModelFactory
import org.threeten.bp.LocalDate

object InjectorUtils {

    fun getAccountsRepository(context: Context): AccountsRepository {
        val db = FTDatabase.getInstance(context)
        return AccountsRepository.getInstance(db.accountDao(), db.currencyDao())
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

    fun getCategoriesRepository(context: Context): CategoriesRepository {
        return CategoriesRepository.getInstance(FTDatabase.getInstance(context).categoryDao())
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