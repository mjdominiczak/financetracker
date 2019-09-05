package com.mancode.financetracker

import com.google.common.truth.Truth.assertThat
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.Report
import org.junit.Test
import org.threeten.bp.LocalDate

class ReportTest {

    @Test
    fun testReport() {
        val dateFrom = LocalDate.of(2019, 1, 1)
        val dateTo = LocalDate.of(2019, 1, 11)
        val transactions: List<TransactionEntity> = arrayListOf(
                TransactionEntity(1, LocalDate.of(2019, 1, 3), TransactionEntity.TYPE_INCOME, "", 3000.0, 0, 1, 1),
                TransactionEntity(1, LocalDate.of(2019, 1, 10), TransactionEntity.TYPE_OUTCOME, "", 700.0, 0, 1, 1)
        )
        val nv1 = NetValue(
                dateFrom, 1000.0, false)
        val nv2 = NetValue(
                dateTo, 2000.0, false)

        val report = Report(dateFrom, dateTo)
        report.setTransactions(transactions)
        report.netValue1 = nv1
        report.netValue2 = nv2
        assertThat(report.income).isEqualTo(3000.0)
        assertThat(report.calculatedOutcome).isEqualTo(2000.0) // nv1 + income - nv2
        assertThat(report.dailyAverage).isEqualTo(130.0) // (calcOutcome - regOutcome) / dayCount
    }
}