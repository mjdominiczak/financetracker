package com.mancode.financetracker.database.pojos;

import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.entity.TransactionEntity;

import org.threeten.bp.LocalDate;

import java.util.List;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;

public class Report {

    private List<TransactionEntity> transactions;
    private NetValue netValue1;
    private NetValue netValue2;

    private LocalDate from;
    private LocalDate to;
    private double income;
    private double registeredOutcome;
    private double unregisteredOutcome;
    private double calculatedOutcome;
    private double balance;

    private long dayCount;
    private double dailyAverage;

    public Report(LocalDate from, LocalDate to) {
        resetDates(from, to);
    }

    public void resetDates(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
        income = 0;
        registeredOutcome = 0;
        unregisteredOutcome = 0;
        calculatedOutcome = 0;
        balance = 0;
        netValue1 = null;
        netValue2 = null;
        dailyAverage = 0;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public double getIncome() {
        return income;
    }

    public double getRegisteredOutcome() {
        return registeredOutcome;
    }

    public double getUnregisteredOutcome() {
        return unregisteredOutcome;
    }

    public double getCalculatedOutcome() {
        return calculatedOutcome;
    }

    public double getBalance() {
        return balance;
    }

    public long getDayCount() {
        return dayCount;
    }

    public NetValue getNetValue1() {
        return netValue1;
    }

    public NetValue getNetValue2() {
        return netValue2;
    }

    public double getDailyAverage() {
        return dailyAverage;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
        calculateIncome();
        calculateOutcomeWithTransactions();
        continueIfDataPresent();
    }

    private void calculateIncome() {
        income = sumTransactions(TransactionEntity.TYPE_INCOME);
    }

    private void calculateOutcomeWithTransactions() {
        registeredOutcome = sumTransactions(TransactionEntity.TYPE_OUTCOME);
    }

    private double sumTransactions(int type) {
        double sum = 0;
        for (TransactionEntity t : transactions) {
            if (t.getType() == type) {
                sum += t.getValue();
            }
        }
        return sum;
    }

    private void updateBalance() {
        balance = income - calculatedOutcome;
    }

    public void setNetValue1(NetValue netValue) {
        this.netValue1 = netValue;
        continueIfDataPresent();
    }

    public void setNetValue2(NetValue netValue) {
        this.netValue2 = netValue;
        continueIfDataPresent();
    }

    private void continueIfDataPresent() {
        if (transactions != null && netValue1 != null && netValue2 != null) {
            calculateOutcomeWithBalances();
            calculateDailyAverage();
            updateBalance();
        }
    }

    private void calculateDailyAverage() {
        dayCount = DAYS.between(netValue1.getDate(), netValue2.getDate());
        dailyAverage = unregisteredOutcome / dayCount;
    }

    private void calculateOutcomeWithBalances() {
        calculatedOutcome = income - (netValue2.getValue() - netValue1.getValue());
        unregisteredOutcome = calculatedOutcome - registeredOutcome;
    }
}

// TODO uwzględnić transakcje w wyliczaniu dziennej delty
