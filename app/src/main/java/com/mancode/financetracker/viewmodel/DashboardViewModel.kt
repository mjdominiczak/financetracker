package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.entity.CurrencyEntity
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.utils.InjectorUtils
import com.mancode.financetracker.workers.sumBalances

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    val actualNetValue: LiveData<Double> = InjectorUtils.getNetValuesRepository(application)
            .actualNetValue

    private val assets: LiveData<List<BalanceEntity>> = InjectorUtils.getBalancesRepository(application)
            .getActualAssets()
    private val liabilities: LiveData<List<BalanceEntity>> = InjectorUtils.getBalancesRepository(application)
            .getActualLiabilities()
    private val accounts = InjectorUtils.getAccountsRepository(application).allAccounts
    private val currencies = InjectorUtils.getCurrencyRepository(application).allCurrencies

    val assetsAccountsNumber: LiveData<Int> = InjectorUtils.getAccountsRepository(application)
            .getAssetsAccountsCount()
    val liabilitiesAccountsNumber: LiveData<Int> = InjectorUtils.getAccountsRepository(application)
            .getLiabilitiesAccountsCount()

    fun getActualAssets(): LiveData<Double?> {
        val result = MediatorLiveData<Double?>()
        result.addSource(assets) {
            result.value = calculateIfComplete(assets, accounts, currencies)
        }
        result.addSource(accounts) {
            result.value = calculateIfComplete(assets, accounts, currencies)
        }
        result.addSource(currencies) {
            result.value = calculateIfComplete(assets, accounts, currencies)
        }
        return result
    }

    fun getActualLiabilities(): LiveData<Double?> {
        val result = MediatorLiveData<Double?>()
        result.addSource(liabilities) {
            result.value = calculateIfComplete(liabilities, accounts, currencies, true)
        }
        result.addSource(accounts) {
            result.value = calculateIfComplete(liabilities, accounts, currencies, true)
        }
        result.addSource(currencies) {
            result.value = calculateIfComplete(liabilities, accounts, currencies, true)
        }
        return result
    }

    private fun calculateIfComplete(balances: LiveData<List<BalanceEntity>>,
                                    accounts: LiveData<List<AccountEntity>>,
                                    currencies: LiveData<List<CurrencyEntity>>,
                                    invert: Boolean = false): Double? {
        val defaultCurrency = PreferenceAccessor.defaultCurrency
        return if (balances.value.isNullOrEmpty() || accounts.value.isNullOrEmpty()
                || currencies.value.isNullOrEmpty())
            null
        else {
            val displayFactor = if (invert) -1.0 else 1.0
            val returnValue = sumBalances(balances.value!!, accounts.value!!, currencies.value!!, defaultCurrency)
            if (returnValue != 0.0) {
                displayFactor * returnValue
            } else {
                // added to avoid display of "-0.0"
                0.0
            }
        }
    }
}