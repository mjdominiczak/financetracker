package com.mancode.financetracker.ui.balances;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mancode.financetracker.R;
import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.entity.BalanceExtended;
import com.mancode.financetracker.ui.UIUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Manveru on 06.09.2017.
 */

public class BalanceRecyclerViewAdapter
        extends RecyclerView.Adapter<BalanceRecyclerViewAdapter.ViewHolder> {

    private RecyclerView mRecyclerView;
    private LinkedHashMap<LocalDate, List<BalanceExtended>> mBalancesMap;
    private List<BalanceExtended> mAllBalances;

    private int mExpandedPosition = -1;

    @Override
    public long getItemId(int position) {
        if (mBalancesMap != null && mBalancesMap.size() >= position) {
            int i = 0;
            for (Map.Entry<LocalDate, List<BalanceExtended>> entry : mBalancesMap.entrySet()){
                if (i == position) {
                    return entry.hashCode();
                } else i++;
            }
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void syncMapWithData() {
        if (mBalancesMap == null) {
            mBalancesMap = new LinkedHashMap<>();
        } else {
            mBalancesMap.clear();
        }
        if (mAllBalances != null) {
            for (BalanceExtended balance : mAllBalances) {
                LocalDate date = balance.checkDate;
                List<BalanceExtended> list;
                if (mBalancesMap.containsKey(date)) {
                    list = mBalancesMap.get(date);
                } else {
                    list = new ArrayList<>();
                    mBalancesMap.put(date, list);
                }
                list.add(balance);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mBalancesMap != null) {
            return mBalancesMap.size();
        } else return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_balance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        if (!(position >= 0 && position < mBalancesMap.size())) {
            throw new IllegalStateException("Position invalid: " + position);
        }
        int i = 0;
        for (Map.Entry<LocalDate, List<BalanceExtended>> entry : mBalancesMap.entrySet()){
            if (i == position) {
                viewHolder.initFromList(entry.getKey(), entry.getValue());
                break;
            } else i++;
        }
    }

    public void setBalances(List<BalanceExtended> balances) {
        mAllBalances = balances;
        syncMapWithData();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        final View mView;
        final ExpandableLayout mLayout;
        final LinearLayout mList;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            mLayout = view.findViewById(R.id.balances_list_expandable);
            mLayout.setOnExpansionUpdateListener(this);
            mList = view.findViewById(R.id.balances_list);
        }

        void initFromList(LocalDate key, List<BalanceExtended> itemList) {
            mList.removeAllViews();
            TextView balanceDate = mView.findViewById(R.id.balance_date);
            balanceDate.setText(DateConverter.INSTANCE.toString(key));
            TextView balanceDaily = mView.findViewById(R.id.balance_daily);
            balanceDaily.setText(UIUtils.getFormattedMoney(calculateDailyBalance(itemList),
                    Currency.getInstance(Locale.getDefault()).getCurrencyCode())); // TODO default currency??? - selectable?
            for (BalanceExtended item : itemList) {
                LinearLayout innerLayout = (LinearLayout) LayoutInflater.from(mView.getContext())
                        .inflate(R.layout.single_balance, mList, false);
                TextView balanceValue = innerLayout.findViewById(R.id.balance_value);
                balanceValue.setText(UIUtils.getFormattedMoney(item.value, item.accountCurrency));
                if (item.value != 0) {
                    int color = item.accountType == 1 ?
                            ContextCompat.getColor(mRecyclerView.getContext(), R.color.colorPositiveValue) :
                            ContextCompat.getColor(mRecyclerView.getContext(), R.color.colorNegativeValue);
                    balanceValue.setTextColor(color);
                }
                TextView balanceAccount = innerLayout.findViewById(R.id.balance_account);
                balanceAccount.setText(item.accountName);
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

        private double calculateDailyBalance(List<BalanceExtended> itemList) {
            double result = 0.0;
            for (BalanceExtended item : itemList) {
                result += ((double) item.accountType) * item.value;
            }
            return result;
        }

    }
}
