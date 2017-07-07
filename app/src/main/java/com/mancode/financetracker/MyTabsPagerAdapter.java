package com.mancode.financetracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class MyTabsPagerAdapter extends FragmentStatePagerAdapter {

    public MyTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment;

        switch (i) {
            case 0:
                fragment = new AccountFragment();
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
