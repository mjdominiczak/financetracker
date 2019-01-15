package com.mancode.financetracker;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.mancode.financetracker.ui.accounts.AccountFragment;
import com.mancode.financetracker.ui.balances.BalanceFragment;
import com.mancode.financetracker.ui.reports.ReportMonthlyFragment;
import com.mancode.financetracker.ui.transactions.TransactionFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyTabsPagerAdapter extends FragmentStatePagerAdapter {

    private final int ACCOUNT_FRAGMENT = 0;
    private final int BALANCE_FRAGMENT = 1;
    private final int TRANSACTION_FRAGMENT = 2;
    private final int REPORTS_FRAGMENT = 3;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    MyTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment;

        switch (i) {
            case ACCOUNT_FRAGMENT:
                fragment = AccountFragment.newInstance();
                break;
            case BALANCE_FRAGMENT:
                fragment = BalanceFragment.newInstance();
                break;
            case TRANSACTION_FRAGMENT:
                fragment = TransactionFragment.newInstance();
                break;
            case REPORTS_FRAGMENT:
                fragment = ReportMonthlyFragment.newInstance();
                break;
            default:
                fragment = null;
                break;
        }

        return fragment;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 4;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
