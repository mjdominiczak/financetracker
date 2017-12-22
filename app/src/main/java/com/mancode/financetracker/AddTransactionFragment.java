package com.mancode.financetracker;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mancode.financetracker.database.DBUtils;
import com.mancode.financetracker.database.DatabaseContract;
import com.mancode.financetracker.ui.SetDateView;


public class AddTransactionFragment extends AddItemFragment {

    private SetDateView mTransactionDate;
    private RadioGroup mRadioGroupType;
    private EditText mDescriptionField;
    private EditText mValueField;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        mTransactionDate = (SetDateView) view.findViewById(R.id.sd_transaction_date);
        mRadioGroupType = (RadioGroup) view.findViewById(R.id.rg_transaction_type);
        mDescriptionField = (EditText) view.findViewById(R.id.tf_description);
        mValueField = (EditText) view.findViewById(R.id.tf_value);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_transaction_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_menu_save) {
                    String date = DBUtils.formatDate(mTransactionDate.getDate());
                    int type = mRadioGroupType.getCheckedRadioButtonId() == R.id.rb_income ?
                            1 : -1;
                    String description = mDescriptionField.getText().toString();
                    double value = Double.parseDouble(mValueField.getText().toString());
                    int account = 7; // TODO hard-coded account
                    int category = 1; // TODO hard-coded category

                    if (TransactionListItem.validate()) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseContract.TransactionEntry.COL_DATE, date);
                        cv.put(DatabaseContract.TransactionEntry.COL_TYPE, type);
                        cv.put(DatabaseContract.TransactionEntry.COL_DESCRIPTION, description);
                        cv.put(DatabaseContract.TransactionEntry.COL_VALUE, value);
                        cv.put(DatabaseContract.TransactionEntry.COL_ACCOUNT_ID, account);
                        cv.put(DatabaseContract.TransactionEntry.COL_CATEGORY_ID, category);
                        getActivity().getContentResolver().insert(DatabaseContract.TransactionEntry.CONTENT_URI, cv);
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
