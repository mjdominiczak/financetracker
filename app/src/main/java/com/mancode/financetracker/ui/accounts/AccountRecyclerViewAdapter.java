package com.mancode.financetracker.ui.accounts;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.AccountEntity;

import java.util.List;

public class AccountRecyclerViewAdapter
        extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> {

    private List<AccountEntity> mAllAccounts;
    private Context mContext;

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mContext = null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mContext = recyclerView.getContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (mAllAccounts != null) {
            AccountEntity account = mAllAccounts.get(position);
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

    public void setAccounts(List<AccountEntity> accounts) {
        mAllAccounts = accounts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;
        final TextView mBalanceView;
        AccountEntity mAccount;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.name);
            mBalanceView = view.findViewById(R.id.account_balance);
        }

        void init(AccountEntity account) {
            mAccount = account;
            mIdView.setText(String.valueOf(mAccount.getId()));
            mContentView.setText(mAccount.getAccountName());
            mBalanceView.setText("TODO"); // TODO
            int color = mAccount.getAccountType() == 1 ?
                    ContextCompat.getColor(mContext, R.color.colorPositiveValue) :
                    ContextCompat.getColor(mContext, R.color.colorNegativeValue);
            mBalanceView.setTextColor(color);
        }
    }
}
