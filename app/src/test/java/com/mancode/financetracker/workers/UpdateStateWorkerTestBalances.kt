package com.mancode.financetracker.workers

import com.google.common.truth.Truth.assertThat
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class UpdateStateWorkerTestBalances {

    private lateinit var account: AccountEntity
    private lateinit var balances: MutableList<BalanceEntity>
    private lateinit var transactions: List<TransactionEntity>

    @Before
    fun prepareData() {
        account = AccountEntity(
                1,
                "asd",
                AccountEntity.TYPE_ASSETS,
                "PLN",
                LocalDate.of(2021, 1, 1),
                null
        )
        balances = mutableListOf(
                BalanceEntity(1, LocalDate.of(2021, 1, 3), 1, 10.0, true),
                BalanceEntity(2, LocalDate.of(2021, 1, 5), 1, 30.0, true),
                BalanceEntity(4, LocalDate.of(2021, 1, 6), 1, 30.0, false),
                BalanceEntity(3, LocalDate.of(2021, 1, 8), 1, 50.0, true)
        )
        transactions = listOf(
                TransactionEntity(1, LocalDate.of(2021, 1, 6), TransactionEntity.TYPE_OUTCOME, "", 2.0, 0, 1, 1),
                TransactionEntity(2, LocalDate.of(2021, 1, 8), TransactionEntity.TYPE_OUTCOME, "", 3.0, 0, 1, 1),
                TransactionEntity(3, LocalDate.of(2021, 1, 10), TransactionEntity.TYPE_OUTCOME, "", 5.0, 0, 1, 1)
        )

    }

    @Test
    fun calculateBalancesForAccount_loadsPreviousDayBalanceWhenInputDateAndFixedBalanceProvided() {
        val balancesList = UpdateStateWorker.calculateBalancesForAccount(
                account,
                balances,
                transactions,
                LocalDate.of(2021, 1, 3)
        )
        assertThat(balancesList.first().value == 10.0).isTrue()
    }

    @Test
    fun calculateBalancesForAccount_loadsPreviousDayBalanceWhenInputDateProvidedAndNoFixedBalance() {
        val balancesList = UpdateStateWorker.calculateBalancesForAccount(
                account,
                balances,
                transactions,
                LocalDate.of(2021, 1, 4)
        )
        assertThat(balancesList.first().value == 10.0).isTrue()
    }

    @Test
    fun calculateBalancesForAccount_datesWithNoInputDate() {
        val balancesList = UpdateStateWorker.calculateBalancesForAccount(
                account,
                balances,
                transactions
        )
        assertThat(balancesList.first().checkDate.isEqual(LocalDate.of(2021, 1, 1))).isTrue()
        assertThat(balancesList.last().checkDate.isEqual(LocalDate.now())).isTrue()
    }

    @Test
    fun calculateBalancesForAccount_datesWithInputDate() {
        val balancesList = UpdateStateWorker.calculateBalancesForAccount(
                account,
                balances,
                transactions,
                LocalDate.of(2021, 1, 6)
        )
        assertThat(balancesList.first().checkDate.isEqual(LocalDate.of(2021, 1, 6))).isTrue()
        assertThat(balancesList.last().checkDate.isEqual(LocalDate.of(2021, 1, 8))).isTrue()
    }

    @Test
    fun calculateBalancesForAccount_datesWithInputDateOnFixedBalance() {
        val balancesList = UpdateStateWorker.calculateBalancesForAccount(
                account,
                balances,
                transactions,
                LocalDate.of(2021, 1, 3)
        )
        assertThat(balancesList.first().checkDate.isEqual(LocalDate.of(2021, 1, 3))).isTrue()
        assertThat(balancesList.last().checkDate.isEqual(LocalDate.of(2021, 1, 3))).isTrue()
    }

    @Test
    fun calculateBalancesForAccount_transactionsIncluded() {
        val balancesList = UpdateStateWorker.calculateBalancesForAccount(
                account,
                balances,
                transactions
        )
        assertThat(balancesList.find { it.checkDate.isEqual(LocalDate.of(2021, 1, 6)) }!!.value == 28.0).isTrue()
        assertThat(balancesList.find { it.checkDate.isEqual(LocalDate.of(2021, 1, 8)) }!!.value == 50.0).isTrue()
        assertThat(balancesList.find { it.checkDate.isEqual(LocalDate.of(2021, 1, 11)) }!!.value == 45.0).isTrue()
    }

    @Test
    fun calculateBalancesForAccount_replacesIncorrectCalculatedBalance() {
        var balanceUnderTest = balances.find { it.id == 4 }
        assertThat(balanceUnderTest!!.value == 30.0).isTrue()
        val balancesList = UpdateStateWorker.calculateBalancesForAccount(
                account,
                balances,
                transactions
        ) // Side effect! :(
        balanceUnderTest = balances.find { it.id == 4 }
        assertThat(balanceUnderTest!!.id).isEqualTo(4)
        assertThat(balanceUnderTest.value).isWithin(0.00001).of(28.0)
    }

}