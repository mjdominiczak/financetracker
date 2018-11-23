package com.mancode.financetracker.ui;

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
}
