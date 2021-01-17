package com.mancode.financetracker.workers

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.database.entity.*
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import org.threeten.bp.LocalDate
import timber.log.Timber

class UpdateStateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val defaultCurrency: String = PreferenceAccessor.defaultCurrency
    private val db: FTDatabase by lazy { FTDatabase.getInstance(applicationContext) }
    private val allCurrencies by lazy { db.currencyDao().allCurrenciesSimple }
    private val allAccounts by lazy { db.accountDao().allAccountsSimple }
    private var allBalances = db.balanceDao().allBalancesSimple
    private val allTransactions by lazy { db.transactionDao().allTransactionsSimple }

    override fun doWork(): Result {

        val start = System.currentTimeMillis()

        val inputAccountIds = inputData.getIntArray(KEY_ACCOUNT_IDS)
        val inputDate = DateConverter.toDate(inputData.getString(KEY_DATE))
        val isPartial = inputDate != null

        var minDate: LocalDate? = null
        var maxDate: LocalDate? = null

        val accounts = if (inputAccountIds != null)
            allAccounts.filter { inputAccountIds.contains(it.id) } else
            allAccounts

        if (accounts.isEmpty()) return Result.failure()

        val balancesToInsert = mutableListOf<BalanceEntity>()
        for (account in accounts) {
            balancesToInsert.addAll(calculateBalancesForAccount(account, allBalances, allTransactions, inputDate))
            val startDate = balancesToInsert.first().checkDate
            val endDate = balancesToInsert.last().checkDate
            if (minDate == null || startDate.isBefore(minDate)) minDate = startDate
            if (maxDate == null || endDate.isAfter(maxDate)) maxDate = endDate

        }
        if (balancesToInsert.isNotEmpty()) {
            balancesToInsert.sortBy { it.checkDate }
            minDate = balancesToInsert.first().checkDate
            maxDate = balancesToInsert.last().checkDate
        }
        if (!isPartial) {
            db.balanceDao().clearNotFixed()
        }
        db.balanceDao().insertAll(balancesToInsert)
        Timber.i("[id=${id}] Balances update: ${(System.currentTimeMillis() - start)}ms")

        allBalances = db.balanceDao().allBalancesSimple
        val netValues = mutableListOf<NetValue>()
        val datesDaily = generateDatesDaily(minDate ?: LocalDate.now(), maxDate ?: LocalDate.now())
        for (date in datesDaily) {
            val balances = allBalances.filter { it.checkDate.isEqual(date) }
            val value = sumBalances(balances, allAccounts, allCurrencies, defaultCurrency)
            val calculated = !allBalances.filter { it.fixed }.any { it.checkDate.isEqual(date) }
            netValues.add(NetValue(date, value, calculated))
        }
        Timber.i("[id=${id}] NetValues update: ${(System.currentTimeMillis() - start)}ms")

        if (!isPartial) {
            db.netValueDao().clear()
        }
        db.netValueDao().insertAll(netValues)

        Timber.i("[id=${id}] Update state end: ${(System.currentTimeMillis() - start)}ms")

        return Result.success()
    }

    companion object {
        const val KEY_DATE = "date"
        const val KEY_ACCOUNT_IDS = "accountIds"

        @VisibleForTesting
        fun calculateBalancesForAccount(
                account: AccountEntity,
                allBalances: List<BalanceEntity>,
                allTransactions: List<TransactionEntity>,
                inputDate: LocalDate? = null
        ): List<BalanceEntity> {
            val openDate = account.openDate
            val closeDate = account.closeDate ?: LocalDate.now()
            val isPartial = inputDate != null
            if (isPartial && (inputDate!!.isBefore(openDate) || inputDate.isAfter(closeDate)))
                return emptyList()

            val balancesToInsert = mutableListOf<BalanceEntity>()
            val fixedKeys = allBalances.filter { it.accountId == account.id && it.fixed }.map { it.checkDate }
            val startDate = inputDate ?: openDate
            val endDate = if (!isPartial) closeDate else
                fixedKeys.find { !it.isBefore(inputDate) } ?: closeDate

            val datesList = generateDatesDaily(startDate, endDate)
            var prevBalance = 0.0
            for (date in datesList) {
                if (!fixedKeys.contains(date)) {
                    /** no fixed balance */
                    if (inputDate != null && date.isEqual(datesList[0])) {
                        /** if input date provided, on first date load previous day balance */
                        try {
                            prevBalance = allBalances.first { it.accountId == account.id && it.checkDate.isEqual(date.minusDays(1)) }.value
                        } catch (e: NoSuchElementException) {
                        }
                    }
                    val balanceId = try {
                        allBalances.first { it.accountId == account.id && it.checkDate.isEqual(date) }.id
                    } catch (e: NoSuchElementException) {
                        0
                    }
                    val transactions = allTransactions.filter { it.accountId == account.id && it.date.isEqual(date) }
                    prevBalance += sumTransactions(transactions)
                    val balanceToAdd = BalanceEntity(balanceId, date, account.id, prevBalance, false)
                    balancesToInsert.add(balanceToAdd)
                } else {
                    /** is fixed balance */
                    balancesToInsert.add(allBalances.find {
                        it.accountId == account.id && it.checkDate.isEqual(date)
                    }!!)
                    prevBalance = allBalances.first { it.accountId == account.id && it.checkDate.isEqual(date) }.value
                }
            }
            return balancesToInsert
        }

    }
}

fun generateDatesDaily(fromInclusive: LocalDate, toInclusive: LocalDate): ArrayList<LocalDate> {
    if (fromInclusive.isAfter(toInclusive)) return ArrayList()

    val dates = ArrayList<LocalDate>()
    var d = fromInclusive
    while (d <= toInclusive) {
        dates.add(d)
        d = d.plusDays(1)
    }
    return dates
}

fun sumTransactions(transactions: List<TransactionEntity>): Double {
    var sum = 0.0
    if (transactions.isEmpty()) return sum
    for (t in transactions) {
        sum += t.type * t.value
    }
    return sum
}

fun sumBalances(balances: List<BalanceEntity>,
                accounts: List<AccountEntity>,
                currencies: List<CurrencyEntity>,
                defaultCurrency: String): Double {
    var value = 0.0
    if (balances.isEmpty()) return value

    val rateEuroToDefault = currencies.find { it.currencySymbol == defaultCurrency }!!.exchangeRate
    for (balance in balances) {
        val account = accounts.find { it.id == balance.accountId }
        val currency = account!!.currency
        val accountCurrencyRate = currencies
                .find { it.currencySymbol == currency }
                ?.exchangeRate ?: 1.0
        val conversionRate = if (currency == defaultCurrency)
            1.0 else
            rateEuroToDefault / accountCurrencyRate
        value += account.accountType.toDouble() * balance.value * conversionRate
    }
    return value
}
