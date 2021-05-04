package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.pojos.Report
import com.mancode.financetracker.utils.InjectorUtils.getNetValuesRepository
import com.mancode.financetracker.utils.InjectorUtils.getTransactionsRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val netValuesRepository = getNetValuesRepository(application)
    private val transactionsRepository = getTransactionsRepository(application)

    val report: Report by lazy {
        val fromDate = getMonthStart(MONTH_THIS)
        val toDate = LocalDate.from(fromDate).plusMonths(1)
        Report(fromDate, toDate)
    }
    val netValues: LiveData<List<NetValue>> = netValuesRepository.netValues

    fun getNetValueClosestToFrom(): LiveData<NetValue> =
        netValuesRepository.getNetValueClosestTo(report.from.minusDays(1))

    fun getNetValueClosestToTo(): LiveData<NetValue> =
        netValuesRepository.getNetValueClosestTo(report.to.minusDays(1))

    fun initReport(whichMonth: Int) {
        val fromDate = getMonthStart(whichMonth)
        report.init(fromDate, LocalDate.from(fromDate).plusMonths(1))
    }

    fun canGetPreviousReport(): Boolean = report.netValue1.date.isBefore(report.from)

    fun canGetNextReport(): Boolean = !report.to.isAfter(LocalDate.now())

    fun getTransactions() = transactionsRepository.getTransactionsForRange(report.from, report.to)

    private fun getMonthStart(whichMonth: Int): LocalDate? {
        return if (whichMonth == MONTH_THIS) LocalDate.now()
            .with(TemporalAdjusters.firstDayOfMonth()) else {
            LocalDate.from(report.from)
                .plusMonths(whichMonth.toLong())
                .with(TemporalAdjusters.firstDayOfMonth())
        }
    }

    companion object {
        const val MONTH_THIS = 0
        const val MONTH_PREVIOUS = -1
        const val MONTH_NEXT = 1
    }
}