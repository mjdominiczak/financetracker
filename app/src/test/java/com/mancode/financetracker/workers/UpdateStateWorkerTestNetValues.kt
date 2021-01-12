package com.mancode.financetracker.workers

import com.google.common.truth.Truth.assertThat
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.entity.CurrencyEntity
import com.mancode.financetracker.workers.UpdateStateWorker.Companion.sumBalances
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class UpdateStateWorkerTestNetValues {

    private lateinit var accounts: List<AccountEntity>
    private lateinit var balances: List<BalanceEntity>
    private lateinit var currencies: List<CurrencyEntity>

    private val defaultCurrency = "PLN"
    private val tolerance = 0.00001

    @Before
    fun prepareData() {
        accounts = listOf(
                AccountEntity(1, "asd", AccountEntity.TYPE_ASSETS, "PLN", LocalDate.of(2021, 1, 1), null),
                AccountEntity(2, "asd", AccountEntity.TYPE_ASSETS, "EUR", LocalDate.of(2021, 1, 1), null),
                AccountEntity(3, "asd", AccountEntity.TYPE_LIABILITIES, "PLN", LocalDate.of(2021, 1, 1), null)
        )
        balances = listOf(
                BalanceEntity(1, LocalDate.of(2021, 1, 1), 1, 10.0, true),
                BalanceEntity(2, LocalDate.of(2021, 1, 1), 2, 10.0, true),
                BalanceEntity(3, LocalDate.of(2021, 1, 1), 3, 20.0, true),
                BalanceEntity(4, LocalDate.of(2021, 1, 3), 1, 20.0, true),
                BalanceEntity(5, LocalDate.of(2021, 1, 3), 2, 10.0, false),
                BalanceEntity(6, LocalDate.of(2021, 1, 3), 3, 25.0, true),
                BalanceEntity(7, LocalDate.of(2021, 1, 5), 1, 30.0, false),
                BalanceEntity(8, LocalDate.of(2021, 1, 5), 2, 20.0, false),
                BalanceEntity(9, LocalDate.of(2021, 1, 5), 3, 0.0, false),
        )
        currencies = listOf(
                CurrencyEntity(defaultCurrency, 5.0, null)
        )

    }

    @Test
    fun sumBalances_allFixed() {
        val balancesFiltered = balances.filter { it.checkDate.isEqual(LocalDate.of(2021,1,1)) }
        val result = sumBalances(balancesFiltered, accounts, currencies, defaultCurrency)
        assertThat(result).isWithin(tolerance).of(40.0)
    }

    @Test
    fun sumBalances_mixed() {
        val balancesFiltered = balances.filter { it.checkDate.isEqual(LocalDate.of(2021,1,3)) }
        val result = sumBalances(balancesFiltered, accounts, currencies, defaultCurrency)
        assertThat(result).isWithin(tolerance).of(45.0)
    }

    @Test
    fun sumBalances_noneFixed() {
        val balancesFiltered = balances.filter { it.checkDate.isEqual(LocalDate.of(2021,1,5)) }
        val result = sumBalances(balancesFiltered, accounts, currencies, defaultCurrency)
        assertThat(result).isWithin(tolerance).of(130.0)
    }
}