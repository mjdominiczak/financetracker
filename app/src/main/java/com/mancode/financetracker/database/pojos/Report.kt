package com.mancode.financetracker.database.pojos

import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.entity.TransactionEntity
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

class Report(from: LocalDate, to: LocalDate) {
    var transactions: List<TransactionEntity>? = null
        set(value) {
            field = value
            if (value != null) {
                calculateIncome()
                calculateOutcomeWithTransactions()
                continueIfDataPresent()
            }
        }
    var netValue1: NetValue? = null
        set(value) {
            field = value
            continueIfDataPresent()
        }
    var netValue2: NetValue? = null
        set(value) {
            field = value
            continueIfDataPresent()
        }
    lateinit var from: LocalDate
        private set
    lateinit var to: LocalDate
        private set
    var income = 0.0
        private set
    var registeredOutcome = 0.0
        private set
    var unregisteredOutcome = 0.0
        private set
    var calculatedOutcome = 0.0
        private set
    var balance = 0.0
        private set
    var dayCount: Long = 0
        private set
    var dailyAverage = 0.0
        private set

    fun init(from: LocalDate, to: LocalDate) {
        this.from = from
        this.to = to
        transactions = null
        income = 0.0
        registeredOutcome = 0.0
        unregisteredOutcome = 0.0
        calculatedOutcome = 0.0
        balance = 0.0
        netValue1 = null
        netValue2 = null
        dailyAverage = 0.0
    }

    private fun calculateIncome() {
        income = sumTransactions(TransactionEntity.TYPE_INCOME)
    }

    private fun calculateOutcomeWithTransactions() {
        registeredOutcome = sumTransactions(TransactionEntity.TYPE_OUTCOME)
    }

    private fun sumTransactions(type: Int): Double =
        transactions!!.filter { it.type == type }.sumOf { it.value }

    fun sumByCategories(type: Int) =
        transactions!!.filter { it.type == type }
            .groupBy({ it.categoryId }) { it.value }
            .mapValues { entry -> entry.value.sum() }
            .toList().sortedBy { it.second }.reversed()

    private fun updateBalance() {
        balance = income - calculatedOutcome
    }

    private fun continueIfDataPresent() {
        if (dataPresent()) {
            calculateOutcomeWithBalances()
            calculateDailyAverage()
            updateBalance()
        }
    }

    fun dataPresent(): Boolean {
        return transactions != null && netValue1 != null && netValue2 != null
    }

    private fun calculateDailyAverage() {
        dayCount = ChronoUnit.DAYS.between(netValue1!!.date, netValue2!!.date)
        dailyAverage = unregisteredOutcome / dayCount
    }

    private fun calculateOutcomeWithBalances() {
        calculatedOutcome = income - (netValue2!!.value - netValue1!!.value)
        unregisteredOutcome = calculatedOutcome - registeredOutcome
    }

    init {
        init(from, to)
    }
}