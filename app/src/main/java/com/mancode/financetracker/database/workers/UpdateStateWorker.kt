package com.mancode.financetracker.database.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.entity.NetValue
import java.util.*

class UpdateStateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val db = FTDatabase.getInstance(applicationContext)
        val dates = db.balanceDao().dateKeys
        val netValues = ArrayList<NetValue>()
        for (date in dates) {
            val balances = db.balanceDao().getBalancesForDate(date)
            var value = 0.0
            for (balance in balances) {
                value += balance.accountType.toDouble() * balance.value
            }
            netValues.add(NetValue(date, value))
        }

        db.netValueDao().insertAll(netValues)

        return Result.success()
    }
}
