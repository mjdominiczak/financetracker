package com.mancode.financetracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountRecyclerViewAdapter extends CursorRecyclerViewAdapter<AccountRecyclerViewAdapter.ViewHolder> {

    public AccountRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        holder.initFromCursor(cursor);
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;
        final TextView mBalanceView;
        AccountListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.name);
            mBalanceView = (TextView) view.findViewById(R.id.account_balance);
        }

        public void initFromCursor(Cursor cursor) {
            mItem = AccountListItem.fromCursor(cursor);
            mIdView.setText(String.valueOf(mItem.getId()));
            mContentView.setText(mItem.getName());
            mBalanceView.setText("TODO"); // TODO
            int color = mItem.getType() == 1 ?
                    ContextCompat.getColor(mContext, R.color.colorPositiveValue) :
                    ContextCompat.getColor(mContext, R.color.colorNegativeValue);
            mBalanceView.setTextColor(color);
        }
    }

}
