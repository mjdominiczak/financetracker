package com.mancode.financetracker.ui.transactions;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Manveru on 18.12.2017.
 */

class TransactionRecyclerViewAdapter
        extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    public static final String TAG = TransactionRecyclerViewAdapter.class.getSimpleName();

    private List<TransactionEntity> mAllTransactions;
    private List<TransactionEntity> mFilteredTransactions;
    private boolean mIsFiltered = false;
    private FilterQuery mFilterQuery;
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
        if (mFilteredTransactions != null && mFilteredTransactions.size() > position) {
            TransactionEntity transaction = mFilteredTransactions.get(position);
            viewHolder.init(transaction);
        } else {
            Log.e(TAG, "");
        }
    }

    @Override
    public int getItemCount() {
        if (mFilteredTransactions != null) {
            return mFilteredTransactions.size();
        } else return 0;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        mAllTransactions = transactions;
        mFilteredTransactions = transactions;  // TODO think through
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new TransactionFilter();
    }

    public String buildFilterQuery(int type, Date from, Date to) {
        mFilterQuery = new FilterQuery(type, from, to);
        return mFilterQuery.getQuery();
    }

    public FilterQuery getFilterQuery() {
        return mFilterQuery;
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

    private class TransactionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String query = charSequence.toString();
            List<TransactionEntity> filteredList = new ArrayList<>();
            if (query.isEmpty()) {
                mIsFiltered = false;
                filteredList = mAllTransactions;
            } else {
                mIsFiltered = true;
                mFilterQuery = new FilterQuery(query);
                for (TransactionEntity transaction : mAllTransactions) {
                    if (mFilterQuery.isMatch(transaction)) {
                        filteredList.add(transaction);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mFilteredTransactions = (ArrayList<TransactionEntity>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
