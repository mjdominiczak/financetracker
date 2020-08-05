package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.utils.InjectorUtils
import org.joda.money.CurrencyUnit
import org.threeten.bp.LocalDate

class FirstRunViewModel(application: Application) : AndroidViewModel(application) {

    private val categoriesRepo = InjectorUtils.getCategoriesRepository(application)
    private val accountsRepo = InjectorUtils.getAccountsRepository(application)
    private val balanceRepo = InjectorUtils.getBalancesRepository(application)

    private val outcomeList = listOf(
            application.getString(R.string.category_name_food),
            application.getString(R.string.category_name_housing),
            application.getString(R.string.category_name_transportation),
            application.getString(R.string.category_name_telecommunication),
            application.getString(R.string.category_name_healthcare),
            application.getString(R.string.category_name_clothes),
            application.getString(R.string.category_name_cosmetics),
            application.getString(R.string.category_name_education),
            application.getString(R.string.category_name_entertainment),
            application.getString(R.string.category_name_miscellaneous)
    )
    private val incomeList = listOf(
            application.getString(R.string.category_name_salary),
            application.getString(R.string.category_name_interest),
            application.getString(R.string.category_name_other)
    )
    private val accountName = application.getString(R.string.account_name_cash)

    val currencyCodesList by lazy { CurrencyUnit.registeredCurrencies().map { it.code } }
    var defaultCurrency = PreferenceAccessor.defaultCurrency
    var openingBalance = 0.0

    fun storeInitialData() {
        if (!PreferenceAccessor.firstRun) return

        categoriesRepo.insertAll(outcomeList.map {
            CategoryEntity(0, it, TransactionEntity.TYPE_OUTCOME, false, null)
        }
        )
        categoriesRepo.insertAll(incomeList.map {
            CategoryEntity(0, it, TransactionEntity.TYPE_INCOME, false, null)
        }
        )
        PreferenceAccessor.defaultCurrency = defaultCurrency
        val accountId = 1
        accountsRepo.insert(
                AccountEntity(accountId, accountName, AccountEntity.TYPE_ASSETS,
                        defaultCurrency, LocalDate.now(), null))
        balanceRepo.insertBalance(
                BalanceEntity(0, LocalDate.now(), accountId, openingBalance, true)
        )

        PreferenceAccessor.firstRun = false
    }

    fun skip() {
        PreferenceAccessor.firstRun = false
    }
}