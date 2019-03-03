package com.mancode.financetracker.database.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.entity.NetValue
import org.threeten.bp.LocalDate

class UpdateStateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val db = FTDatabase.getInstance(applicationContext)
        val dateKeys: List<LocalDate> = db.balanceDao().dateKeys
        if (dateKeys.isEmpty()) return Result.failure()

        val datesDaily: ArrayList<LocalDate> =
                generateDatesDaily(dateKeys[0], LocalDate.now())

        val netValues = ArrayList<NetValue>()
        for (date in datesDaily) {
            var value = 0.0
            val calculated: Boolean
            if (date in dateKeys) {
                val balances = db.balanceDao().getBalancesForDate(date)
                for (balance in balances) {
                    value += balance.accountType.toDouble() * balance.value
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
        // TODO check why executed twice

        db.netValueDao().clear()
        db.netValueDao().insertAll(netValues)

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
