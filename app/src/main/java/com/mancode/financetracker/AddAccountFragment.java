package com.mancode.financetracker;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mancode.financetracker.database.DBUtils;
import com.mancode.financetracker.database.DatabaseContract;
import com.mancode.financetracker.ui.SetDateView;


public class AddAccountFragment extends AddItemFragment {

    private EditText mNameField;
    private RadioGroup mRadioGroupType;
    private SetDateView mOpenDate;
    private SetDateView mCloseDate;
    private CheckBox mCheckBoxClosed;

    public AddAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);

        mNameField = (EditText) view.findViewById(R.id.tf_account_name);
        mRadioGroupType = (RadioGroup) view.findViewById(R.id.rg_account_type);
        mOpenDate = (SetDateView) view.findViewById(R.id.sd_account_open_date);
        mCloseDate = (SetDateView) view.findViewById(R.id.sd_account_close_date);
        mCheckBoxClosed = (CheckBox) view.findViewById(R.id.cb_account_closed);

        mCheckBoxClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCloseDate.setEnabled(mCheckBoxClosed.isChecked());
            }
        });

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_account_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_menu_save) {
                    String name = mNameField.getText().toString();
                    String type = mRadioGroupType.getCheckedRadioButtonId() == R.id.rb_assets ?
                            "+" : "-";
                    String openDate = DBUtils.formatDate(mOpenDate.getDate());
                    String closeDate = DBUtils.formatDate(mCloseDate.getDate());
                    String currency = "1";

                    if (AccountListItem.validate(name, type)) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseContract.AccountEntry.COL_NAME, name);
                        cv.put(DatabaseContract.AccountEntry.COL_TYPE, type);
                        cv.put(DatabaseContract.AccountEntry.COL_CURRENCY_ID, currency);
                        cv.put(DatabaseContract.AccountEntry.COL_OPEN_DATE, openDate);
                        cv.put(DatabaseContract.AccountEntry.COL_CLOSE_DATE, closeDate);
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
