package com.mancode.financetracker.ui.transactions;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.viewmodel.TransactionViewModel;
import com.mancode.financetracker.ui.SetDateView;
import com.mancode.financetracker.ui.UIUtils;

import java.util.Date;

import static com.mancode.financetracker.ui.transactions.FilterQuery.TYPE_ALL;
import static com.mancode.financetracker.ui.transactions.FilterQuery.TYPE_INCOME;
import static com.mancode.financetracker.ui.transactions.FilterQuery.TYPE_OUTCOME;

/**
 * Created by Manveru on 23.11.2017.
 */

public class TransactionFragment extends Fragment {

    public TransactionRecyclerViewAdapter mAdapter;

    public TransactionFragment() { }

    public static TransactionFragment newInstance() {
        return new TransactionFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TransactionViewModel transactionViewModel =
                ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false); // TODO layout to change?
        View recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView rView = (RecyclerView) recyclerView;
            rView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new TransactionRecyclerViewAdapter();
            rView.setAdapter(mAdapter);
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> UIUtils.showFullScreenDialog(
                getFragmentManager(), AddTransactionFragment.newInstance()));
        return view;
    }

    private class FilterDialog {

        private Spinner mTransactionTypeSpinner;
        private SetDateView mFromDate;
        private SetDateView mToDate;
        private AlertDialog.Builder mBuilder;

        void show() {
            mBuilder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.fragment_transaction_filter, null);
            mTransactionTypeSpinner = dialogView.findViewById(R.id.sp_transaction_filter_type);
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

        // TODO additional spinner for predefined timespan selection

        private Date getFromDate() {
            return mFromDate.getDate();
        }

        private Date getToDate() {
            return mToDate.getDate();
        }
    }

}
