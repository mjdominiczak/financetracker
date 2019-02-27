package com.mancode.financetracker.database.pojos;

import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.entity.TransactionFull;

import org.threeten.bp.LocalDate;

import java.util.List;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;

public class Report {

    private List<TransactionFull> transactions;
    private NetValue netValue1;
    private NetValue netValue2;

    private LocalDate from;
    private LocalDate to;
    private double income;
    private double outcome;
    private double calculatedOutcome;
    private double balance;
    private double total;

    private long dayCount;
    private double dailyAverage;

    private NetValue netValueFrom;
    private NetValue netValueTo;

    public Report(LocalDate from, LocalDate to) {
        resetDates(from, to);
    }

    public void resetDates(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
        income = 0;
        outcome = 0;
        calculatedOutcome = 0;
        balance = 0;
        total = 0;
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

    public double getOutcome() {
        return outcome;
    }

    public double getCalculatedOutcome() {
        return calculatedOutcome;
    }

    public double getBalance() {
        return balance;
    }

    public double getTotal() {
        return total;
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

    public NetValue getNetValueFrom() {
        return netValueFrom;
    }

    public NetValue getNetValueTo() {
        return netValueTo;
    }

    public void setTransactions(List<TransactionFull> transactions) {
        this.transactions = transactions;
        calculateIncome();
        calculateOutcomeWithTransactions();
        updateBalance();
        continueIfDataPresent();
    }

    private void calculateIncome() {
        income = sumTransactions(TransactionEntity.TYPE_INCOME);
    }

    private void calculateOutcomeWithTransactions() {
        outcome = sumTransactions(TransactionEntity.TYPE_OUTCOME);
    }

    private double sumTransactions(int type) {
        double sum = 0;
        for (TransactionFull t : transactions) {
            if (t.transaction.type == type) {
                sum += t.transaction.value;
            }
        }
        return sum;
    }

    private void updateBalance() {
        balance = income - outcome;
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
        if (netValue1 != null && netValue2 != null) {
            calculateDailyAverage();
            calculateSyntheticNetValues();
            calculateOutcomeWithBalances();
        }
    }

    private void calculateDailyAverage() {
        dayCount = DAYS.between(netValue1.getDate(), netValue2.getDate());
        dailyAverage = (netValue2.getValue() - netValue1.getValue()) / dayCount;
    }

    private void calculateSyntheticNetValues() { // TODO TEST
        if (from.isEqual(netValue1.getDate())) {
            double valueFrom = netValue1.getValue() +
                    dailyAverage * DAYS.between(netValue1.getDate(), from);
            netValueFrom = new NetValue(from, valueFrom);
        } else {
            netValueFrom = netValue1;
        }
        if (to.isEqual(netValue2.getDate())) {
            double valueTo = netValue2.getValue() +
                    dailyAverage * DAYS.between(netValue2.getDate(), to);
            netValueTo = new NetValue(to, valueTo);
        } else {
            netValueTo = netValue2;
        }
    }

    private void calculateOutcomeWithBalances() {
        calculatedOutcome = income - (netValueTo.getValue() - netValueFrom.getValue());
    }
}

// TODO uwzględnić transakcje w wyliczaniu dziennej delty
