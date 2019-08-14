package com.mancode.financetracker.ui.transactions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.pojos.TransactionFull;
import com.mancode.financetracker.ui.UIUtils;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manveru on 18.12.2017.
 */

class TransactionRecyclerViewAdapter
        extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    private static final String TAG = TransactionRecyclerViewAdapter.class.getSimpleName();

    private List<TransactionFull> allTransactions;
    private List<TransactionFull> filteredTransactions;
    private boolean isFiltered = false;
    private FilterQuery filterQuery;
    private Context context;
    private ModifyRequestListener modifyRequestListener;

    TransactionRecyclerViewAdapter(Context context,
                                   ModifyRequestListener modifyRequestListener) {
        this.context = context;
        this.modifyRequestListener = modifyRequestListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        if (filteredTransactions != null && filteredTransactions.size() > position) {
            TransactionFull transaction = filteredTransactions.get(position);
            viewHolder.init(transaction);
        } else {
            Log.e(TAG, "");
        }
    }

    @Override
    public int getItemCount() {
        if (filteredTransactions != null) {
            return filteredTransactions.size();
        } else return 0;
    }

    public void setTransactions(List<TransactionFull> transactions) {
        allTransactions = transactions;
        filteredTransactions = transactions;  // TODO think through
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new TransactionFilter();
    }

    String buildFilterQuery(int type, LocalDate from, LocalDate to) {
        filterQuery = new FilterQuery(type, from, to);
        return filterQuery.getQuery();
    }

    FilterQuery getFilterQuery() {
        return filterQuery;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvValue;
        private TextView tvDescription;
        private ImageButton menuButton;
        private TransactionFull mTransaction;

        ViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.transaction_date);
            tvValue = view.findViewById(R.id.transaction_value);
            tvDescription = view.findViewById(R.id.transaction_description);
            menuButton = view.findViewById(R.id.transaction_menu_button);
        }

        void init(TransactionFull transaction) {
            mTransaction = transaction;
            tvDate.setText(DateConverter.toString(mTransaction.getTransaction().getDate()));
            tvValue.setText(UIUtils.getFormattedMoney(
                    mTransaction.getTransaction().getValue(), mTransaction.getCurrency()));
            int color = mTransaction.getTransaction().getType() == TransactionEntity.TYPE_INCOME ?
                    ContextCompat.getColor(context, R.color.colorPositiveValue) :
                    ContextCompat.getColor(context, R.color.colorNegativeValue);
            tvValue.setTextColor(color);
            tvDescription.setText(mTransaction.getTransaction().getDescription());
            menuButton.setOnClickListener(this::showTransactionPopup);
        }

        void showTransactionPopup(View view) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.transaction_actions, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_edit_transaction:
                        if (modifyRequestListener != null) {
                            modifyRequestListener.onEditRequested(mTransaction.getTransaction());
                        }
                        return true;
                    case R.id.action_delete_transaction:
                        if (modifyRequestListener != null) {
                            modifyRequestListener.onDeleteRequested(mTransaction.getTransaction());
                        }
                        return true;
                }
                return false;
            });
            popup.show();
        }
    }

    private class TransactionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String query = charSequence.toString();
            List<TransactionFull> filteredList = new ArrayList<>();
            if (query.isEmpty()) {
                isFiltered = false;
                filteredList = allTransactions;
            } else {
                isFiltered = true;
                filterQuery = new FilterQuery(query);
                for (TransactionFull transaction : allTransactions) {
                    if (filterQuery.isMatch(transaction)) {
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
            filteredTransactions = (ArrayList<TransactionFull>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public interface ModifyRequestListener {
        void onEditRequested(TransactionEntity transaction);
        void onDeleteRequested(TransactionEntity transaction);
    }
}
