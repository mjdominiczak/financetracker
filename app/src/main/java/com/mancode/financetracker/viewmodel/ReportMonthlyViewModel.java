package com.mancode.financetracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mancode.financetracker.database.FTDatabase;
import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.pojos.Report;
import com.mancode.financetracker.repository.ReportMonthlyRepository;

import org.threeten.bp.LocalDate;

import java.util.List;

public class ReportMonthlyViewModel extends AndroidViewModel {

    private ReportMonthlyRepository repository;
    private Report visibleReport;

    public void setVisibleReport(Report visibleReport) {
        this.visibleReport = visibleReport;
    }

    public ReportMonthlyViewModel(@NonNull Application application) {
        super(application);
        FTDatabase db = FTDatabase.getInstance(application);
        repository = ReportMonthlyRepository.getInstance(db.transactionDao(), db.netValueDao());
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return repository.getTransactionsForRange(visibleReport.getFrom(), visibleReport.getTo());
    }

    public LiveData<NetValue> getNetValueBefore(LocalDate date) {
        return repository.getNetValueBefore(date);
    }

    public LiveData<NetValue> getNetValueAfter(LocalDate date) {
        return repository.getNetValueAfter(date);
    }

}
