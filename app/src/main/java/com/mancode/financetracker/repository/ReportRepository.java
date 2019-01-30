package com.mancode.financetracker.repository;

import com.mancode.financetracker.database.dao.NetValueDao;
import com.mancode.financetracker.database.dao.TransactionDao;
import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.entity.TransactionFull;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;

public class ReportRepository {

    private static ReportRepository instance;

    private TransactionDao transactionDao;
    private NetValueDao netValueDao;

    private ReportRepository(TransactionDao transactionDao, NetValueDao netValueDao) {
        this.transactionDao = transactionDao;
        this.netValueDao = netValueDao;
    }

    public static ReportRepository getInstance(TransactionDao transactionDao, NetValueDao netValueDao) {
        if (instance == null) {
            synchronized (ReportRepository.class) {
                if (instance == null) {
                    instance = new ReportRepository(transactionDao, netValueDao);
                }
            }
        }
        return instance;
    }

    public LiveData<List<TransactionFull>> getTransactionsForRange(Date from, Date to) {
        return transactionDao.getTransactionsFromRange(from, to);
    }

    public LiveData<List<NetValue>> getNetValues() {
        return netValueDao.getValues();
    }

    public LiveData<NetValue> getNetValueBefore(Date date) {
        return netValueDao.getValueBefore(date);
    }

    public LiveData<NetValue> getNetValueAfter(Date date) {
        return netValueDao.getValueAfter(date);
    }
}
