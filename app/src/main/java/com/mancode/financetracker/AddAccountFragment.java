package com.mancode.financetracker;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.ui.SetDateView;

import java.util.Date;


public class AddAccountFragment extends AddItemFragment {

    private EditText mNameField;
    private RadioGroup mRadioGroupType;
    private SetDateView mOpenDate;
    private SetDateView mCloseDate;
    private CheckBox mCheckBoxClosed;

    private AccountViewModel mAccountViewModel;

    public AddAccountFragment() { }

    public static AddAccountFragment newInstance() {
        return new AddAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);

        mNameField = view.findViewById(R.id.tf_account_name);
        mRadioGroupType = view.findViewById(R.id.rg_account_type);
        mOpenDate = view.findViewById(R.id.sd_account_open_date);
        mCloseDate = view.findViewById(R.id.sd_account_close_date);
        mCheckBoxClosed = view.findViewById(R.id.cb_account_closed);

        mCheckBoxClosed.setOnClickListener(v -> mCloseDate.setEnabled(mCheckBoxClosed.isChecked()));

        Toolbar toolbar = view.findViewById(R.id.add_account_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                String name = mNameField.getText().toString();
                int type = mRadioGroupType.getCheckedRadioButtonId() == R.id.rb_assets ?
                        1 : -1;
                Date openDate = mOpenDate.getDate();
                Date closeDate = mCloseDate.getDate();
                int currency = 1; // TODO hard-coded currency
                AccountEntity account = new AccountEntity(
                        0, // not set
                        name,
                        type,
                        currency,
                        openDate,
                        closeDate
                );

                if (true) { // TODO validate
                    mAccountViewModel.insert(account);
                    dismiss();
                }
            }
            return false;
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        return view;
    }
}
