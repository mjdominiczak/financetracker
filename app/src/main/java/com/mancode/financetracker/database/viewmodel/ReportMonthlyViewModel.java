package com.mancode.financetracker.database.viewmodel;

import android.app.Application;

import com.mancode.financetracker.database.FTDatabase;
import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.entity.TransactionFull;
import com.mancode.financetracker.database.pojos.Report;
import com.mancode.financetracker.repository.ReportRepository;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ReportMonthlyViewModel extends AndroidViewModel {

    private ReportRepository repository;
    private Report visibleReport;

    public void setVisibleReport(Report visibleReport) {
        this.visibleReport = visibleReport;
    }

    public ReportMonthlyViewModel(@NonNull Application application) {
        super(application);
        FTDatabase db = FTDatabase.getInstance(application);
        repository = ReportRepository.getInstance(db.transactionDao(), db.netValueDao());
    }

    public LiveData<List<TransactionFull>> getTransactions() {
        return repository.getTransactionsForRange(visibleReport.getFrom(), visibleReport.getTo());
    }

    public LiveData<NetValue> getNetValueBefore(Date date) {
        return repository.getNetValueBefore(date);
    }

    public LiveData<NetValue> getNetValueAfter(Date date) {
        return repository.getNetValueAfter(date);
    }

}
