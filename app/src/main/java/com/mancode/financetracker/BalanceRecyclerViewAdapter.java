package com.mancode.financetracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mancode.financetracker.database.DatabaseContract;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Manveru on 06.09.2017.
 */

class BalanceRecyclerViewAdapter extends CursorRecyclerViewAdapter<BalanceRecyclerViewAdapter.ViewHolder> {

    private RecyclerView mRecyclerView;
    private LinkedHashMap<String, List<BalanceListItem>> mBalancesMap;

    private int mExpandedPosition = -1;

    BalanceRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mBalancesMap.size() >= position) {
            if (mBalancesMap != null) {
                int i = 0;
                for (Map.Entry<String, List<BalanceListItem>> entry : mBalancesMap.entrySet()){
                    if (i == position) {
                        return entry.hashCode();
                    } else i++;
                }
            }
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    void initDataFromCursor() {
        if (mBalancesMap != null) {
            mBalancesMap.clear();
        }
        mBalancesMap = new LinkedHashMap<>();
        Cursor cursor = getCursor();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex(DatabaseContract.BalanceEntry.COL_CHECK_DATE));
                List<BalanceListItem> list;
                if (mBalancesMap.containsKey(date)) {
                    list = mBalancesMap.get(date);
                } else {
                    list = new ArrayList<>();
                    mBalancesMap.put(date, list);
                }
                list.add(BalanceListItem.fromCursor(cursor));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public int getItemCount() {
        if (mBalancesMap != null) {
            return mBalancesMap.size();
        }
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_balance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {}

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (!(position >= 0 && position < mBalancesMap.size())) {
            throw new IllegalStateException("Position invalid: " + position);
        }

        if (mBalancesMap != null) {
            int i = 0;
            for (Map.Entry<String, List<BalanceListItem>> entry : mBalancesMap.entrySet()){
                if (i == position) {
                    viewHolder.initFromList(entry.getKey(), entry.getValue());
                    break;
                } else i++;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        final View mView;
        final ExpandableLayout mLayout;
        final LinearLayout mList;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            mLayout = (ExpandableLayout) view.findViewById(R.id.balances_list_expandable);
            mLayout.setOnExpansionUpdateListener(this);
            mList = (LinearLayout) view.findViewById(R.id.balances_list);
        }

        void initFromList(String key, List<BalanceListItem> itemList) {
            mList.removeAllViews();
            TextView balanceDate = (TextView) mView.findViewById(R.id.balance_date);
            balanceDate.setText(key);
            TextView balanceDaily = (TextView) mView.findViewById(R.id.balance_daily);
            balanceDaily.setText(String.format(Locale.getDefault(), "%.2f", BalanceListItem.calculateDailyBalance(itemList)));
            for (BalanceListItem item : itemList) {
                LinearLayout innerLayout = (LinearLayout) LayoutInflater.from(mView.getContext())
                        .inflate(R.layout.single_balance, mList, false);
                TextView balanceValue = (TextView) innerLayout.findViewById(R.id.balance_value);
                balanceValue.setText(String.format(Locale.getDefault(), "%.2f", item.getBalance()));
                TextView balanceAccount = (TextView) innerLayout.findViewById(R.id.balance_account);
                balanceAccount.setText(item.getAccount());
                mList.addView(innerLayout);
            }
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == mExpandedPosition) {
                mLayout.collapse();
                mExpandedPosition = -1;
            } else {
                if (mExpandedPosition != -1) {
                    ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mExpandedPosition);
                    if (holder != null) {
                        holder.mLayout.collapse();
                    }
                }
                mLayout.expand();
                mExpandedPosition = getAdapterPosition();
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            Log.d("Expandable layout", "State: " + state);
            mRecyclerView.smoothScrollToPosition(getAdapterPosition());
        }
    }
}
