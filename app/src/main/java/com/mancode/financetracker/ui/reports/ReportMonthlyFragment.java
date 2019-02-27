package com.mancode.financetracker.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.entity.TransactionFull;
import com.mancode.financetracker.database.pojos.Report;
import com.mancode.financetracker.database.viewmodel.ReportMonthlyViewModel;
import com.mancode.financetracker.ui.UIUtils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

public class ReportMonthlyFragment extends Fragment {

    private static final int MONTH_THIS = 0;
    private static final int MONTH_PREVIOUS = -1;
    private static final int MONTH_NEXT = 1;

    private ReportMonthlyViewModel viewModel;
    private Report report;
    private Calendar calendar;

    private TextView tvNetValueDate1;
    private TextView tvNetValueDate2;
    private TextView tvNetValue1;
    private TextView tvNetValue2;
    private TextView tvIncome;
    private TextView tvOutcome;
    private TextView tvCalcOutcome;
    private TextView tvBalance;
    private TextView tvTotal;
    private TextView tvDailyAverage;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private Button btnRange;
    private TextView tvNetValueFromDate;
    private TextView tvNetValueToDate;
    private TextView tvNetValueFrom;
    private TextView tvNetValueTo;

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
        resetObservers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_monthly, container, false);
        btnRange = view.findViewById(R.id.btn_report_range);
        tvNetValueDate1 = view.findViewById(R.id.tv_net_value_date1);
        tvNetValueDate2 = view.findViewById(R.id.tv_net_value_date2);
        tvNetValue1 = view.findViewById(R.id.tv_net_value1);
        tvNetValue2 = view.findViewById(R.id.tv_net_value2);
        tvIncome = view.findViewById(R.id.tv_report_income);
        tvOutcome = view.findViewById(R.id.tv_report_outcome);
        tvCalcOutcome = view.findViewById(R.id.tv_report_calc_outcome);
        tvBalance = view.findViewById(R.id.tv_report_balance);
        tvTotal = view.findViewById(R.id.tv_report_total);
        tvDailyAverage = view.findViewById(R.id.tv_daily_average);
        tvNetValueFromDate = view.findViewById(R.id.tv_net_value_from_date);
        tvNetValueToDate = view.findViewById(R.id.tv_net_value_to_date);
        tvNetValueFrom = view.findViewById(R.id.tv_net_value_from);
        tvNetValueTo = view.findViewById(R.id.tv_net_value_to);

        btnPrev = view.findViewById(R.id.btn_prev_report);
        btnPrev.setOnClickListener(v -> {
            ReportMonthlyFragment.this.initReport(MONTH_PREVIOUS);
            resetObservers();
        });
        btnNext = view.findViewById(R.id.btn_next_report);
        btnNext.setOnClickListener(v -> {
            initReport(MONTH_NEXT);
            resetObservers();
        });
        btnRange.setOnClickListener(v -> {
            initReport(MONTH_THIS);
            resetObservers();
        });

        initReport(MONTH_THIS);
        return view;
    }

    private void resetObservers() {
        LiveData<List<TransactionFull>> transactions = viewModel.getTransactions();
        transactions.removeObservers(this);
        transactions.observe(ReportMonthlyFragment.this, this::setTransactions);
        LiveData<NetValue> netValueBefore1 = viewModel.getNetValueBefore(report.getTo()); // TODO alghoritm for choosing netvalues
        netValueBefore1.removeObservers(this);
        netValueBefore1.observe(this, this::setNetValue2);
        LiveData<NetValue> netValueBefore2 = viewModel.getNetValueBefore(report.getFrom());
        netValueBefore2.removeObservers(this);
        netValueBefore2.observe(this, this::setNetValue1);
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
        updateNetValueViews();
    }

    private void updateViews() {
        String reportRange = report.getFrom().toString() + " - " + report.getTo().toString();
        btnRange.setText(reportRange);
        tvIncome.setText(UIUtils.getFormattedMoney(report.getIncome(), "PLN")); // TODO hardcoded currency
        tvOutcome.setText(UIUtils.getFormattedMoney(report.getOutcome(), "PLN")); // TODO hardcoded currency
        tvBalance.setText(UIUtils.getFormattedMoney(report.getBalance(), "PLN")); // TODO hardcoded currency
        tvTotal.setText(UIUtils.getFormattedMoney(report.getTotal(), "PLN")); // TODO hardcoded currency

        if (report.getTo().isAfter(LocalDate.now())) {
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
    }

    private void setNetValue1(NetValue netValue) {
        report.setNetValue1(netValue);
        updateNetValueViews();
    }

    private void setNetValue2(NetValue netValue) {
        report.setNetValue2(netValue);
        updateNetValueViews();
    }

    private void updateNetValueViews() {
        boolean value1Set = report.getNetValue1() != null;
        boolean value2Set = report.getNetValue2() != null;
        if (value1Set) {
            tvNetValueDate1.setText(report.getNetValue1().getDate().toString());
            tvNetValue1.setText(UIUtils.getFormattedMoney(
                    report.getNetValue1().getValue(), "PLN")); // TODO hardcoded currency
        } else {
            tvNetValueDate1.setText("n/a");
            tvNetValue1.setText("n/a");
        }
        if (value2Set) {
            tvNetValueDate2.setText(report.getNetValue2().getDate().toString());
            tvNetValue2.setText(UIUtils.getFormattedMoney(
                    report.getNetValue2().getValue(), "PLN")); // TODO hardcoded currency
        } else {
            tvNetValueDate2.setText("n/a");
            tvNetValue2.setText("n/a");
        }
        if (value1Set && value2Set) {
            tvNetValueFromDate.setText(report.getNetValue1().getDate().toString());
            tvNetValueToDate.setText(report.getNetValue2().getDate().toString());
            tvNetValueFrom.setText(UIUtils.getFormattedMoney(
                    report.getNetValue1().getValue(), "PLN")); // TODO hardcoded currency
            tvNetValueTo.setText(UIUtils.getFormattedMoney(
                    report.getNetValue2().getValue(), "PLN")); // TODO hardcoded currency
            tvDailyAverage.setText(UIUtils.getFormattedMoney(
                    report.getDailyAverage(), "PLN")); // TODO hardcoded currency
            tvCalcOutcome.setText(UIUtils.getFormattedMoney(
                    report.getCalculatedOutcome(), "PLN")); // TODO hardcoded currency
        }
    }

    private LocalDate getMonthStart(int whichMonth) {
        if (whichMonth == MONTH_THIS)
            return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        else {
            return LocalDate.from(report.getFrom())
                    .plusMonths(whichMonth)
                    .with(TemporalAdjusters.firstDayOfMonth());
        }
    }

    private LocalDate getMonthEnd(int whichMonth) {
        if (whichMonth == MONTH_THIS)
            return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        else {
            return LocalDate.from(report.getFrom())
                    .plusMonths(whichMonth)
                    .with(TemporalAdjusters.lastDayOfMonth());
        }
    }

}
