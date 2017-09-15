package com.mancode.financetracker;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mancode.financetracker.database.DatabaseContract;

/**
 * Created by Manveru on 03.09.2017.
 */

public class BalanceFragment extends LoaderFragment {

    public BalanceFragment() {
    }

    public static BalanceFragment newInstance() {
        return new BalanceFragment();
    }

    static final String[] BALANCES_PROJECTION = new String[]{
            DatabaseContract.BalanceEntry._ID,
            DatabaseContract.BalanceEntry.COL_ACCOUNT_ID,
            DatabaseContract.BalanceEntry.COL_BALANCE,
            DatabaseContract.BalanceEntry.COL_CHECK_DATE,
            DatabaseContract.BalanceEntry.COL_CURRENCY_ID,
            DatabaseContract.BalanceEntry.COL_FIXED
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false); // TODO layout to change?
        View recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView rView = (RecyclerView) recyclerView;
            rView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new BalanceRecyclerViewAdapter(getActivity(), null);
            rView.setAdapter(mAdapter);
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFullScreenDialog(new AddBalanceFragment());
            }
        });
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DatabaseContract.BalanceEntry.CONTENT_URI;
        String select = "(" + DatabaseContract.BalanceEntry.COL_BALANCE + " NOTNULL)";
        return new CursorLoader(
                getActivity(),
                uri,
                BALANCES_PROJECTION,
                select,
                null,
                DatabaseContract.BalanceEntry._ID);
    }
}