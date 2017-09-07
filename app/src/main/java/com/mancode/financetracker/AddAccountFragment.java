package com.mancode.financetracker;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mancode.financetracker.database.DatabaseContract;


public class AddAccountFragment extends AddItemFragment {

    public AddAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_account_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_menu_save) {
                    String name = ((EditText) getView().findViewById(R.id.tf_account_name)).getText().toString();
                    String type = ((EditText) getView().findViewById(R.id.tf_account_type)).getText().toString();

                    if (AccountListItem.validate(name, type)) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_NAME, name);
                        cv.put(DatabaseContract.AccountEntry.COLUMN_NAME_TYPE, type);
                        getActivity().getContentResolver().insert(DatabaseContract.AccountEntry.CONTENT_URI, cv);
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
}
