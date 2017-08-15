package com.mancode.financetracker;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mancode.financetracker.database.DatabaseHelper;


public class AddAccountFragment extends DialogFragment {


    public AddAccountFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        if (dialog != null && dialog.getWindow() != null) {
//            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        }
//        return dialog;
//    }
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_account_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO: app context?
                if (item.getItemId() == R.id.action_menu_save) {
                    String name = ((EditText)getView().findViewById(R.id.tf_account_name)).getText().toString();
                    String type = ((EditText)getView().findViewById(R.id.tf_account_type)).getText().toString();

                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity().getApplicationContext());
                    if (validateAccount(name, type)) {
                        dbHelper.addAccount(name, type);
                        dismiss();
                    }
                }
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        return view;
    }

    private boolean validateAccount(String name, String type) {
        return !name.isEmpty() && !type.isEmpty();
    }

}
