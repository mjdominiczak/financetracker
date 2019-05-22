package com.mancode.financetracker;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mancode.financetracker.ui.accounts.AccountFragment;
import com.mancode.financetracker.ui.balances.BalanceFragment;
import com.mancode.financetracker.ui.reports.ReportMonthlyFragment;
import com.mancode.financetracker.ui.transactions.TransactionFragment;

public class MyTabsPagerAdapter extends FragmentStatePagerAdapter {

    public static final int ACCOUNT_FRAGMENT = 0;
    public static final int BALANCE_FRAGMENT = 1;
    public static final int TRANSACTION_FRAGMENT = 2;
    public static final int REPORTS_FRAGMENT = 3;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    MyTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {

        Fragment fragment;

        switch (i) {
            default:
            case ACCOUNT_FRAGMENT:
                fragment = AccountFragment.Companion.newInstance();
                break;
            case BALANCE_FRAGMENT:
                fragment = BalanceFragment.Companion.newInstance();
                break;
            case TRANSACTION_FRAGMENT:
                fragment = TransactionFragment.newInstance();
                break;
            case REPORTS_FRAGMENT:
                fragment = ReportMonthlyFragment.newInstance();
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
