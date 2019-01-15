package com.mancode.financetracker.ui.reports;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.TransactionFull;
import com.mancode.financetracker.database.pojos.Report;
import com.mancode.financetracker.database.viewmodel.ReportMonthlyViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ReportMonthlyFragment extends Fragment {

    private static final int MONTH_THIS = 0;
    private static final int MONTH_PREVIOUS = -1;
    private static final int MONTH_NEXT = 1;

    private ReportMonthlyViewModel viewModel;
    private Report report;
    private Calendar calendar = Calendar.getInstance();

    private TextView tvReportRange;
    private TextView tvIncome;
    private TextView tvOutcome;
    private TextView tvBalance;
    private TextView tvTotal;
    private ImageButton btnPrev;
    private ImageButton btnNext;

    public ReportMonthlyFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new ReportMonthlyFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ReportMonthlyViewModel.class);
        viewModel.setVisibleReport(report);
        viewModel.getTransactions().observe(this,
                this::setTransactions);
//        viewModel.getNetValueBefore() TODO
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_monthly, container, false);
        tvReportRange = view.findViewById(R.id.btn_report_range);
        tvIncome = view.findViewById(R.id.tv_report_income);
        tvOutcome = view.findViewById(R.id.tv_report_outcome);
        tvBalance = view.findViewById(R.id.tv_report_balance);
        tvTotal = view.findViewById(R.id.tv_report_total);

        btnPrev = view.findViewById(R.id.btn_prev_report);
        btnPrev.setOnClickListener(v -> {
            ReportMonthlyFragment.this.initReport(MONTH_PREVIOUS);
            viewModel.getTransactions().removeObservers(this);
            viewModel.getTransactions().observe(ReportMonthlyFragment.this,
                    this::setTransactions);
        });
        btnNext = view.findViewById(R.id.btn_next_report);
        btnNext.setOnClickListener(v -> {
            initReport(MONTH_NEXT);
            viewModel.getTransactions().removeObservers(this);
            viewModel.getTransactions().observe(this,
                    this::setTransactions);
        });

        initReport(MONTH_THIS);
        return view;
    }

    private void initReport(int whichMonth) {
        if (report == null) {
            report = new Report(getMonthStart(whichMonth), getMonthEnd(whichMonth));
        } else {
            report.resetDates(getMonthStart(whichMonth), getMonthEnd(whichMonth));
        }
        updateViews();
    }

    public void setTransactions(List<TransactionFull> transactions) {
        report.setTransactions(transactions);
        updateViews();
    }

    private void updateViews() {
        DateFormat format = SimpleDateFormat.getDateInstance();
        String reportRange = format.format(report.getFrom()) + " - " + format.format(report.getTo());
        tvReportRange.setText(reportRange);
        tvIncome.setText(String.valueOf(report.getIncome()));
        tvOutcome.setText(String.valueOf(report.getOutcome()));
        tvBalance.setText(String.valueOf(report.getBalance()));
        tvTotal.setText(String.valueOf(report.getTotal()));

        if (report.getTo().after(Calendar.getInstance().getTime())) {
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
    }

    private Date getMonthStart(int whichMonth) {
        if (report != null) calendar.setTime(report.getFrom());
        calendar.add(Calendar.MONTH, whichMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getMonthEnd(int whichMonth) {
        if (report != null) calendar.setTime(report.getFrom());
        calendar.add(Calendar.MONTH, whichMonth + 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

}
