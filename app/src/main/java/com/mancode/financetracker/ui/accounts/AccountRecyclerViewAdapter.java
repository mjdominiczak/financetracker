package com.mancode.financetracker.ui.accounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.views.AccountExtended;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AccountRecyclerViewAdapter
        extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> {

    private List<AccountExtended> mAllAccounts;
    private Context mContext;

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mContext = null;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mContext = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (mAllAccounts != null) {
            AccountExtended account = mAllAccounts.get(position);
            viewHolder.init(account);
        } else {
            viewHolder.mContentView.setText("No account yet!");
        }
    }

    @Override
    public int getItemCount() {
        if (mAllAccounts != null) {
            return mAllAccounts.size();
        } else return 0;
    }

    public void setAccounts(List<AccountExtended> accounts) {
        mAllAccounts = accounts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;
        final TextView mBalanceView;
        AccountExtended mAccount;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.name);
            mBalanceView = view.findViewById(R.id.account_balance);
        }

        void init(AccountExtended account) {
            mAccount = account;
            mIdView.setText(String.valueOf(mAccount.id));
            mContentView.setText(mAccount.accountName);
            if (mAccount.balanceCheckDate != null) {
                mBalanceView.setText(String.format(Locale.getDefault(), "%.2f", mAccount.balanceValue));
                if (mAccount.balanceValue != 0) {
                    int color = mAccount.accountType == 1 ?
                            ContextCompat.getColor(mContext, R.color.colorPositiveValue) :
                            ContextCompat.getColor(mContext, R.color.colorNegativeValue);
                    mBalanceView.setTextColor(color);
                }
            } else {
                mBalanceView.setText("n/a"); //TODO refactor - different info
            }
        }
    }
}
