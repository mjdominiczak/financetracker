package com.mancode.financetracker;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Manveru on 07.09.2017.
 */

abstract class AddItemFragment extends DialogFragment {

    @Override
    public void dismiss() {
        hideKeyboard();
        super.dismiss();
    }

    private void hideKeyboard() {
        View view = null;
        if (this.getView() != null) {
            view = this.getView().findFocus();
        }
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
