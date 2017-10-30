package com.mancode.financetracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mancode.financetracker.database.DatabaseContract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Manveru on 06.09.2017.
 */

class BalanceRecyclerViewAdapter extends CursorRecyclerViewAdapter<BalanceRecyclerViewAdapter.ViewHolder> {

    private LinkedHashMap<String, List<BalanceListItem>> mBalancesMap;

    BalanceRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (!(position >= 0 && position < mBalancesMap.size())) {
            throw new IllegalStateException("Position invalid: " + position);
        }
        if (position == 0) {
            if (viewHolder.mView.getLayoutParams() instanceof RecyclerView.LayoutParams) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.mView.getLayoutParams();
                int value = (int) viewHolder.mView.getResources().getDimension(R.dimen.card_margin);
                params.setMargins(0, value, 0, value);
                viewHolder.mView.setLayoutParams(params);
            }
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final LinearLayout mLayout;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mLayout = (LinearLayout) view.findViewById(R.id.balances_list);
        }

        void initFromList(String key, List<BalanceListItem> itemList) {
            mLayout.removeAllViews();
            TextView balanceDate = (TextView) mView.findViewById(R.id.balance_date);
            balanceDate.setText(key);
            TextView balanceDaily = (TextView) mView.findViewById(R.id.balance_daily);
            balanceDaily.setText(String.format(Locale.getDefault(), "%.2f", BalanceListItem.calculateDailyBalance(itemList)));
            for (BalanceListItem item : itemList) {
                LinearLayout innerLayout = (LinearLayout) LayoutInflater.from(mView.getContext())
                        .inflate(R.layout.single_balance, mLayout, false);
                TextView balanceValue = (TextView) innerLayout.findViewById(R.id.balance_value);
                balanceValue.setText(String.format(Locale.getDefault(), "%.2f", item.getBalance()));
                TextView balanceAccount = (TextView) innerLayout.findViewById(R.id.balance_account);
                balanceAccount.setText(item.getAccount());
                mLayout.addView(innerLayout);
            }
        }
    }
}
