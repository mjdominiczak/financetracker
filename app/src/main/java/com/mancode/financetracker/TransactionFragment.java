package com.mancode.financetracker;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mancode.financetracker.database.viewmodel.TransactionViewModel;
import com.mancode.financetracker.ui.UIUtils;

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
}
