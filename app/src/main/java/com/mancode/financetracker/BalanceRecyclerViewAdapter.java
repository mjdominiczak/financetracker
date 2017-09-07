package com.mancode.financetracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Manveru on 06.09.2017.
 */

class BalanceRecyclerViewAdapter extends CursorRecyclerViewAdapter<BalanceRecyclerViewAdapter.ViewHolder> {
    public BalanceRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_account, parent, false); // TODO refactor R.layout.fragment_account
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.initFromCursor(cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        BalanceListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.name);
        }

        public void initFromCursor(Cursor cursor) {
            mItem = BalanceListItem.fromCursor(cursor);
            mIdView.setText(String.valueOf(mItem.getId()));
            mContentView.setText(mItem.getCheckDate() + ": " + mItem.getAccount() + "\n" + mItem.getBalance());
        }
    }
}
