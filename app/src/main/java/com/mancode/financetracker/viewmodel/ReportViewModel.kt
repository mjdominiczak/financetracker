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

    val report: Report by lazy { Report(getMonthStart(MONTH_THIS), getMonthEnd(MONTH_THIS)) }
    val netValues: LiveData<List<NetValue>> = netValuesRepository.netValues
    val keyedNetValues: LiveData<List<NetValue>> = netValuesRepository.keyedNetValues

    fun getNetValueBeforeFrom(): LiveData<NetValue> =
            netValuesRepository.getNetValueBefore(report.from)

    fun getNetValueBeforeTo(): LiveData<NetValue> =
            netValuesRepository.getNetValueBefore(report.to)

    fun initReport(whichMonth: Int) {
        report.init(getMonthStart(whichMonth), getMonthEnd(whichMonth))
    }

    fun getTransactions() = transactionsRepository.getTransactionsForRange(report.from, report.to)

    private fun getMonthStart(whichMonth: Int): LocalDate? {
        return if (whichMonth == MONTH_THIS) LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()) else {
            LocalDate.from(report.from)
                    .plusMonths(whichMonth.toLong())
                    .with(TemporalAdjusters.firstDayOfMonth())
        }
    }

    private fun getMonthEnd(whichMonth: Int): LocalDate? {
        return if (whichMonth == MONTH_THIS) LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()) else {
            LocalDate.from(report.from)
                    .plusMonths(whichMonth.toLong())
                    .with(TemporalAdjusters.lastDayOfMonth())
        }
    }

    companion object {
        const val MONTH_THIS = 0
        const val MONTH_PREVIOUS = -1
        const val MONTH_NEXT = 1
    }
}