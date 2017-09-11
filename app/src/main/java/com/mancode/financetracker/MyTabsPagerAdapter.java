package com.mancode.financetracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class MyTabsPagerAdapter extends FragmentStatePagerAdapter {

    private final int ACCOUNT_FRAGMENT = 0;
    private final int BALANCE_FRAGMENT = 1;

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
            default:
                fragment = new AccountFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "TAB " + (position + 1);
    }
}
