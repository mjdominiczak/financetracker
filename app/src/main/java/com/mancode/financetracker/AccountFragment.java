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

import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.ui.UIUtils;

/**
 * Created by Manveru on 03.09.2017.
 */

public class AccountFragment extends Fragment {

    public AccountRecyclerViewAdapter mAdapter;

    public AccountFragment() { }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AccountViewModel accountViewModel =
                ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
        accountViewModel.getAllAccounts().observe(this,
                accounts -> mAdapter.setAccounts(accounts));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false);
        View recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView rView = (RecyclerView) recyclerView;
            rView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new AccountRecyclerViewAdapter();
            rView.setAdapter(mAdapter);
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> UIUtils.showFullScreenDialog(
                getFragmentManager(), AddAccountFragment.newInstance()));
        return view;
    }
}
