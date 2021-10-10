package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.Report
import com.mancode.financetracker.utils.InjectorUtils.getNetValuesRepository
import com.mancode.financetracker.utils.InjectorUtils.getTransactionsRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val netValuesRepository = getNetValuesRepository(application)
    private val transactionsRepository = getTransactionsRepository(application)

    val netValues: LiveData<List<NetValue>> = netValuesRepository.netValues

    private val _report = MediatorLiveData<Report>()
    val report: LiveData<Report>
        get() = _report

    private val reportFrom =
        MutableLiveData(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()))

    /**
     * Function transforming "from" date to "to" date
     */
    private fun LocalDate.transform(): LocalDate = this.plusMonths(1)

    private val transactionsForRange: LiveData<List<TransactionEntity>> =
        Transformations.switchMap(reportFrom) { fromDate ->
            transactionsRepository.getTransactionsForRange(fromDate, fromDate.transform())
        }

    private val netValueClosestToFrom: LiveData<NetValue> =
        Transformations.switchMap(reportFrom) { fromDate ->
            netValuesRepository.getNetValueClosestTo(fromDate.minusDays(1))
        }

    private val netValueClosestToTo: LiveData<NetValue> =
        Transformations.switchMap(reportFrom) { fromDate ->
            netValuesRepository.getNetValueClosestTo(fromDate.transform().minusDays(1))
        }

    private var transactions: List<TransactionEntity>? = null
    private var netValue1: NetValue? = null
    private var netValue2: NetValue? = null

    init {
        _report.addSource(transactionsForRange) {
            this.transactions = it
            if (dataPresent()) _report.value = getNewReport()
        }
        _report.addSource(netValueClosestToFrom) {
            this.netValue1 = it
            if (dataPresent()) _report.value = getNewReport()
        }
        _report.addSource(netValueClosestToTo) {
            this.netValue2 = it
            if (dataPresent()) _report.value = getNewReport()
        }
    }

    private fun getNewReport(): Report {
        val newReport = Report(reportFrom.value, reportFrom.value!!.transform())
        newReport.setTransactions(transactions)
        newReport.netValue1 = netValue1
        newReport.netValue2 = netValue2
        return newReport
    }

    private fun dataPresent(): Boolean =
        transactions != null && netValue1 != null && netValue2 != null

    private fun clearData() {
        transactions = null
        netValue1 = null
        netValue2 = null
    }

    fun canGetPreviousReport(): Boolean =
        netValues.value?.first()?.date!!.isBefore(reportFrom.value)

    fun canGetNextReport(): Boolean = !reportFrom.value!!.transform().isAfter(LocalDate.now())

    fun requestPreviousReport(): Boolean {
        return if (canGetPreviousReport()) {
            reportFrom.value = reportFrom.value?.minusMonths(1)
            clearData()
            true
        } else false
    }

    fun requestNextReport(): Boolean {
        return if (canGetNextReport()) {
            reportFrom.value = reportFrom.value?.plusMonths(1)
            clearData()
            true
        } else false
    }

    fun requestActualReport() {
        reportFrom.value = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
        clearData()
    }

    fun getReportRange() = Pair(reportFrom.value!!, reportFrom.value!!.transform())
}