package com.mancode.financetracker.database.pojos;

import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.entity.TransactionFull;

import java.util.Date;
import java.util.List;

public class Report {

    private List<TransactionFull> transactions;

    private Date from;
    private Date to;
    private double income;
    private double outcome;
    private double balance;
    private double total;

    public Report(Date from, Date to) {
        resetDates(from, to);
    }

    public void resetDates(Date from, Date to) {
        this.from = from;
        this.to = to;
        income = 0;
        outcome = 0;
        balance = 0;
        total = 0;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public double getIncome() {
        return income;
    }

    public double getOutcome() {
        return outcome;
    }

    public double getBalance() {
        return balance;
    }

    public double getTotal() {
        return total;
    }

    public void setTransactions(List<TransactionFull> transactions) {
        this.transactions = transactions;
        calculateIncome();
        calculateOutcome();
        updateBalance();
    }

    private void calculateIncome() {
        income = sumTransactions(TransactionEntity.TYPE_INCOME);
    }

    private void calculateOutcome() {
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
}
