package com.mancode.financetracker;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.database.viewmodel.BalanceViewModel;
import com.mancode.financetracker.ui.SetDateView;

import java.util.Date;
import java.util.List;

/**
 * Created by Manveru on 07.09.2017.
 */

public class AddBalanceFragment extends AddItemFragment {

    private SetDateView mCheckDate;
    private Spinner mAccountSpinner;
    private EditText mBalanceValue;

    private ArrayAdapter<AccountEntity> mAccountSpinnerAdapter;

    private AccountViewModel mAccountViewModel;
    private BalanceViewModel mBalanceViewModel;

    public AddBalanceFragment() { }

    public static AddBalanceFragment newInstance() {
        return new AddBalanceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
        mBalanceViewModel = ViewModelProviders.of(getActivity()).get(BalanceViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_balance, container, false);

        mCheckDate = view.findViewById(R.id.tf_balance_check_date);
        mAccountSpinner = view.findViewById(R.id.spinner_balance_account);
        mBalanceValue = view.findViewById(R.id.tf_balance);

        List<AccountEntity> accountEntityList = mAccountViewModel.getAllAccounts().getValue();
        if (accountEntityList != null) {
            mAccountSpinnerAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    accountEntityList
            );
        }
        mAccountSpinner.setAdapter(mAccountSpinnerAdapter);

        Toolbar toolbar = view.findViewById(R.id.add_balance_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                Date checkDate = mCheckDate.getDate();
                int accountId = mAccountSpinnerAdapter.getItem(
                        mAccountSpinner.getSelectedItemPosition()).getId();
                double value = Double.parseDouble(mBalanceValue.getText().toString());
                BalanceEntity balance = new BalanceEntity(
                        0, // not set
                        checkDate,
                        accountId,
                        value,
                        true
                );

                if (true) { // TODO validate
                    mBalanceViewModel.insert(balance);
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
