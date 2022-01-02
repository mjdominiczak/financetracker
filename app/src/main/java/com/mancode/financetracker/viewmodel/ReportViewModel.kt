package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_INCOME
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_OUTCOME
import com.mancode.financetracker.database.pojos.Report
import com.mancode.financetracker.utils.InjectorUtils.getCategoriesRepository
import com.mancode.financetracker.utils.InjectorUtils.getNetValuesRepository
import com.mancode.financetracker.utils.InjectorUtils.getTransactionsRepository
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val netValuesRepository = getNetValuesRepository(application)
    private val transactionsRepository = getTransactionsRepository(application)
    private val categoriesRepository = getCategoriesRepository(application)

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

    private val _previousReportAvailable = MediatorLiveData<Boolean>()
    val previousReportAvailable: LiveData<Boolean>
        get() = _previousReportAvailable

    val nextReportAvailable = Transformations.map(reportFrom) {
        !it.transform().isAfter(LocalDate.now())
    }

    init {
        _previousReportAvailable.addSource(netValues) {
            if (it.isNotEmpty()) {
                _previousReportAvailable.value = it.first().date.isBefore(reportFrom.value)
            }
        }
        _previousReportAvailable.addSource(reportFrom) {
            if (!netValues.value.isNullOrEmpty()) {
                _previousReportAvailable.value =
                    netValues.value!!.first().date.isBefore(it)
            }
        }
    }

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

    private val incomeCategories: LiveData<List<CategoryEntity>> =
        categoriesRepository.incomeCategories
    private var incomeCategoriesList: List<CategoryEntity>? = null
    private val outcomeCategories: LiveData<List<CategoryEntity>> =
        categoriesRepository.outcomeCategories
    private var outcomeCategoriesList: List<CategoryEntity>? = null

    val sumByIncomeCategories = MediatorLiveData<List<Pair<CategoryEntity, Double>>>()
    val sumByOutcomeCategories = MediatorLiveData<List<Pair<CategoryEntity, Double>>>()

    init {
        sumByIncomeCategories.addSource(incomeCategories) {
            incomeCategoriesList = it
        }
        sumByIncomeCategories.addSource(report) { report ->
            if (incomeCategoriesList != null) {
                sumByIncomeCategories.value =
                    report.sumByCategories(TYPE_INCOME).map { pair: Pair<Int, Double> ->
                        Pair(incomeCategoriesList!!.first { it.id == pair.first }, pair.second)
                    }
            }
        }
        sumByOutcomeCategories.addSource(outcomeCategories) {
            outcomeCategoriesList = it
        }
        sumByOutcomeCategories.addSource(report) { report ->
            if (outcomeCategoriesList != null) {
                val outcomeSumList = report.sumByCategories(TYPE_OUTCOME).map {
                    if (it.first == 1) // TODO weryfikacja ID kategorii, do której mają być przypisane niezarejestrowane wydatki
                        Pair(it.first, it.second + report.unregisteredOutcome)
                    else it
                }.sortedByDescending { it.second }
                sumByOutcomeCategories.value =
                    outcomeSumList.map { pair: Pair<Int, Double> ->
                        Pair(outcomeCategoriesList!!.first { it.id == pair.first }, pair.second)
                    }
            }
        }
    }

    private fun getNewReport(): Report {
        val newReport = Report(reportFrom.value!!, reportFrom.value!!.transform())
        newReport.transactions = transactions
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

    fun requestPreviousReport() {
        if (previousReportAvailable.value == true) {
            reportFrom.value = reportFrom.value?.minusMonths(1)
            clearData()
        }
    }

    fun requestNextReport() {
        if (nextReportAvailable.value == true) {
            reportFrom.value = reportFrom.value?.plusMonths(1)
            clearData()
        }
    }

    fun requestActualReport() {
        reportFrom.value = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
        clearData()
    }
}