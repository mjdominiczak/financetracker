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

    public static String getFormattedMoney(double value, String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        formatter.setCurrency(currency);
        formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        return formatter.format(value);
    }
}
