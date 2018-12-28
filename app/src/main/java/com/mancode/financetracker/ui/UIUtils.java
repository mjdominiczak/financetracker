package com.mancode.financetracker.ui;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Manveru on 03.02.2018.
 */

public class UIUtils {

    public static void showFullScreenDialog (FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        transaction.addToBackStack(null);
//        addAccountFragment.show(transaction, "addAccountFragment");
//
        transaction
                .setCustomAnimations(
                    android.R.animator.fade_in, android.R.animator.fade_out,
                    android.R.animator.fade_in, android.R.animator.fade_out)
                .add(android.R.id.content, fragment).addToBackStack(null).commit();
    }

    public static String getFormattedMoney(double value, String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        formatter.setCurrency(currency);
        formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        return formatter.format(value);
    }
}
