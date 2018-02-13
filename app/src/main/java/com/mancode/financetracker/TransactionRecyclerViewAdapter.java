package com.mancode.financetracker;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.TransactionEntity;

import java.util.List;
import java.util.Locale;

/**
 * Created by Manveru on 18.12.2017.
 */

class TransactionRecyclerViewAdapter
        extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder> {

    private List<TransactionEntity> mAllTransactions;
    private Context mContext;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mContext = recyclerView.getContext();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mContext = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (mAllTransactions != null) {
            TransactionEntity transaction = mAllTransactions.get(position);
            viewHolder.init(transaction);
        } else {
            viewHolder.tvDescription.setText("No transactions entered!");
        }
    }

    @Override
    public int getItemCount() {
        if (mAllTransactions != null) {
            return mAllTransactions.size();
        } else return 0;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.mAllTransactions = transactions;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvValue;
        private TextView tvDescription;
        private TransactionEntity mTransaction;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.transaction_date);
            tvValue = view.findViewById(R.id.transaction_value);
            tvDescription = view.findViewById(R.id.transaction_description);
        }

        void init(TransactionEntity transaction) {
            mTransaction = transaction;
            tvDate.setText(DateConverter.toString(mTransaction.getDate()));
            tvValue.setText(String.format(Locale.getDefault(), "%.2f", mTransaction.getValue()));
            int color = mTransaction.getType() == 1 ?
                    ContextCompat.getColor(mContext, R.color.colorPositiveValue) :
                    ContextCompat.getColor(mContext, R.color.colorNegativeValue);
            tvValue.setTextColor(color);
            tvDescription.setText(mTransaction.getDescription());
        }
    }
}
