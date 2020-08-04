package com.mancode.financetracker.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import org.threeten.bp.LocalDate

class UpdateStateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val start = System.currentTimeMillis()

        val db = FTDatabase.getInstance(applicationContext)
        db.netValueDao().clear()
        val dateKeys: List<LocalDate> = db.balanceDao().dateKeys
        if (dateKeys.isEmpty()) return Result.failure()

        val datesDaily: ArrayList<LocalDate> =
                generateDatesDaily(dateKeys[0], LocalDate.now())
        val currency = PreferenceAccessor.defaultCurrency
        val rateEuroToDefault = db.currencyDao().getExchangeRateForCurrency(currency)

        val netValues = ArrayList<NetValue>()
        for (date in datesDaily) {
            var value = 0.0
            val calculated: Boolean
            if (date in dateKeys) {
                val balances = db.balanceDao().getBalancesForDate(date)
                for (balance in balances) {
                    val conversionRate = if (balance.accountCurrency == currency) 1.0 else {
                        rateEuroToDefault / db.currencyDao().getExchangeRateForCurrency(balance.accountCurrency)
                    }
                    value += balance.accountType.toDouble() * balance.value * conversionRate
                }
                calculated = false
            } else {
                value = netValues[netValues.size - 1].value
                val transactions = db.transactionDao().getTransactionsForDate(date)
                for (transaction in transactions) {
                    value += transaction.value * transaction.type.toDouble()
                }
                calculated = true
            }
            netValues.add(NetValue(date, value, calculated))
        }

        db.netValueDao().insertAll(netValues)

        println("${(System.currentTimeMillis() - start) / 1000}s")

        return Result.success()
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
