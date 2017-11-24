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
 * Created by Manveru on 23.11.2017.
 */

public class TransactionFragment extends LoaderFragment {

    public TransactionFragment() {
    }

    public static TransactionFragment newInstance() {
        return new TransactionFragment();
    }

    static final String[] TRANSACTIONS_PROJECTION = new String[]{
            DatabaseContract.TransactionEntry.TBL_NAME + "." + DatabaseContract.TransactionEntry._ID,
            DatabaseContract.TransactionEntry.COL_DATE,
            DatabaseContract.TransactionEntry.COL_TYPE,
            DatabaseContract.TransactionEntry.COL_DESCRIPTION,
            DatabaseContract.TransactionEntry.COL_VALUE,
            DatabaseContract.AccountEntry.TBL_NAME + "." + DatabaseContract.AccountEntry._ID,
            DatabaseContract.AccountEntry.COL_NAME,
            DatabaseContract.AccountEntry.COL_TYPE,
            DatabaseContract.CategoryEntry.TBL_NAME + "." + DatabaseContract.CategoryEntry._ID,
            DatabaseContract.CategoryEntry.COL_CATEGORY
    };

    static final String TRANSACTIONS_SORT_ORDER =
            "date(" + DatabaseContract.TransactionEntry.COL_DATE + ") DESC, " +
            DatabaseContract.TransactionEntry.TBL_NAME + "." + DatabaseContract.TransactionEntry._ID + " DESC";

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        syncAdapterWithCursor();
    }

    public void syncAdapterWithCursor() {
        if (mAdapter instanceof BalanceRecyclerViewAdapter) {
            ((BalanceRecyclerViewAdapter) mAdapter).initDataFromCursor();
        }
    }

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
        Uri uri = DatabaseContract.TransactionEntryJoined.CONTENT_URI;
        return new CursorLoader(
                getActivity(),
                uri,
                TRANSACTIONS_PROJECTION,
                null,
                null,
                TRANSACTIONS_SORT_ORDER);
    }
}
