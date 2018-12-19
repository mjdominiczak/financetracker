package com.mancode.financetracker.ui.transactions;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.viewmodel.TransactionViewModel;
import com.mancode.financetracker.ui.SetDateView;
import com.mancode.financetracker.ui.UIUtils;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.mancode.financetracker.ui.transactions.FilterQuery.TYPE_ALL;
import static com.mancode.financetracker.ui.transactions.FilterQuery.TYPE_INCOME;
import static com.mancode.financetracker.ui.transactions.FilterQuery.TYPE_OUTCOME;

/**
 * Created by Manveru on 23.11.2017.
 */

public class TransactionFragment extends Fragment implements TransactionRecyclerViewAdapter.DeleteRequestListener {

    private TransactionRecyclerViewAdapter mAdapter;
    private TransactionViewModel transactionViewModel;

    public TransactionFragment() { }

    public static TransactionFragment newInstance() {
        return new TransactionFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        transactionViewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        transactionViewModel.getAllTransactions().observe(this,
                transactions -> mAdapter.setTransactions(transactions));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_transaction_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_filter:
                new FilterDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false); // TODO layout to change?
        View recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView rView = (RecyclerView) recyclerView;
            rView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new TransactionRecyclerViewAdapter(context, this);
            rView.setAdapter(mAdapter);
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> UIUtils.showFullScreenDialog(
                getFragmentManager(), AddTransactionFragment.newInstance()));
        return view;
    }

    @Override
    public void onDeleteRequested(TransactionEntity transaction) {
        transactionViewModel.deleteTransaction(transaction);
    }

    private class FilterDialog {

        private Spinner mTransactionTypeSpinner;
        private Spinner mTransactionTimespanSpinner;
        private SetDateView mFromDate;
        private SetDateView mToDate;
        private AlertDialog.Builder mBuilder;

        void show() {
            mBuilder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.fragment_transaction_filter, null);
            mTransactionTypeSpinner = dialogView.findViewById(R.id.sp_transaction_filter_type);
            mTransactionTimespanSpinner = dialogView.findViewById(R.id.sp_transaction_filter_timespan);
            mTransactionTimespanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    handleTimespan(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            mFromDate = dialogView.findViewById(R.id.sd_transaction_filter_from);
            mToDate = dialogView.findViewById(R.id.sd_transaction_filter_to);
            FilterQuery query = mAdapter.getFilterQuery();
            if (query != null) {
                mTransactionTypeSpinner.setSelection(getTypePosition(query.getType()));
                Date fromDate = query.getFromDate();
                if (fromDate != null) mFromDate.setDate(fromDate);
                Date toDate = query.getToDate();
                if (toDate != null) mToDate.setDate(toDate);
            }
            mBuilder.setTitle(R.string.title_transaction_filter)
                    .setView(dialogView)
                    .setNeutralButton(R.string.neutral_filter, (dialog, which) ->
                            mAdapter.getFilter().filter(FilterQuery.getEmptyQuery()))
                    .setNegativeButton(R.string.negative_filter, (dialog, which) -> { })
                    .setPositiveButton(R.string.positive_filter, (dialog, which) -> {
                        String newQuery =
                                mAdapter.buildFilterQuery(getType(), getFromDate(), getToDate());
                        mAdapter.getFilter().filter(newQuery);
                    })
                    .show();
        }

        private int getType() {
            switch (mTransactionTypeSpinner.getSelectedItemPosition()) {
                case 1:
                    return TYPE_INCOME;
                case 2:
                    return TYPE_OUTCOME;
                case 0:
                default:
                    return TYPE_ALL;
            }
        }

        private int getTypePosition(int type) {
            switch (type) {
                case TYPE_INCOME:
                    return 1;
                case TYPE_OUTCOME:
                    return 2;
                case TYPE_ALL:
                default:
                    return 0;
            }
        }

        private void handleTimespan(int position) {
            Calendar calendar = Calendar.getInstance();
            switch (position) {
                case 0: // UNCONSTRAINED
                    mToDate.resetDate();
                    mFromDate.resetDate();
                    break;
                case 1: // LAST WEEK
                    mToDate.setDate(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    mFromDate.setDate(calendar.getTime());
                    break;
                case 2: // LAST MONTH
                    mToDate.setDate(calendar.getTime());
                    calendar.add(Calendar.MONTH, -1);
                    mFromDate.setDate(calendar.getTime());
                    break;
                case 3: // THIS MONTH
                    mToDate.setDate(calendar.getTime());
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    mFromDate.setDate(calendar.getTime());
                    break;
                case 4: // PREVIOUS MONTH
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    mToDate.setDate(calendar.getTime());
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    mFromDate.setDate(calendar.getTime());
                    break;
                case 5: // THIS YEAR
                    mToDate.setDate(calendar.getTime());
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                    mFromDate.setDate(calendar.getTime());
                    break;
                case 6: // CUSTOM
                    mToDate.setEnabled(true);
                    mFromDate.setEnabled(true);
                    return;
            }
            mToDate.setEnabled(false);
            mFromDate.setEnabled(false);
        }

        private Date getFromDate() {
            return mFromDate.getDate();
        }

        private Date getToDate() {
            return mToDate.getDate();
        }
    }

}
