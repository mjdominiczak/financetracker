package com.mancode.financetracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class MyTabsPagerAdapter extends FragmentStatePagerAdapter {

    private final int ACCOUNT_FRAGMENT = 0;
    private final int BALANCE_FRAGMENT = 1;
    private final int TRANSACTION_FRAGMENT = 2;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public MyTabsPagerAdapter(FragmentManager fm) {
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
            default:
                fragment = null;
                break;
        }

        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 3;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
