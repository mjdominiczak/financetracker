package com.mancode.financetracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Manveru on 18.12.2017.
 */

class TransactionRecyclerViewAdapter extends CursorRecyclerViewAdapter<TransactionRecyclerViewAdapter.ViewHolder> {

    TransactionRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.initFromCursor(cursor);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvValue;
        private TextView tvDescription;
        private TransactionListItem mItem;

        ViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.transaction_date);
            tvValue = (TextView) view.findViewById(R.id.transaction_value);
            tvDescription = (TextView) view.findViewById(R.id.transaction_description);
        }

        void initFromCursor(Cursor cursor) {
            mItem = TransactionListItem.fromCursor(cursor);
            tvDate.setText(mItem.getTransactionDate());
            tvValue.setText(String.format(Locale.getDefault(), "%.2f", mItem.getValue()));
            int color = mItem.getTransactionType() == 1 ?
                    ContextCompat.getColor(mContext, R.color.colorPositiveValue) :
                    ContextCompat.getColor(mContext, R.color.colorNegativeValue);
            tvValue.setTextColor(color);
            tvDescription.setText(mItem.getDescription());
        }
    }
}
