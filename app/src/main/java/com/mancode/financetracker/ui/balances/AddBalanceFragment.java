package com.mancode.financetracker.ui.balances;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mancode.financetracker.AddItemFragment;
import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.database.viewmodel.BalanceViewModel;
import com.mancode.financetracker.database.views.AccountExtended;
import com.mancode.financetracker.ui.SetDateView;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by Manveru on 07.09.2017.
 */

public class AddBalanceFragment extends AddItemFragment {

    private SetDateView checkDate;
    private Spinner accountSpinner;
    private EditText valueField;

    private ArrayAdapter<AccountExtended> accountSpinnerAdapter;

    private AccountViewModel accountViewModel;
    private BalanceViewModel balanceViewModel;

    public AddBalanceFragment() { }

    static AddBalanceFragment newInstance() {
        return new AddBalanceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            accountViewModel = ViewModelProviders.of(activity).get(AccountViewModel.class);
            balanceViewModel = ViewModelProviders.of(activity).get(BalanceViewModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_balance, container, false);

        checkDate = view.findViewById(R.id.tf_balance_check_date);
        accountSpinner = view.findViewById(R.id.spinner_balance_account);
        valueField = view.findViewById(R.id.tf_balance);
        valueField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    valueField.setError(getString(R.string.error_value_empty));
                }
            }
        });

        List<AccountExtended> accountEntityList = accountViewModel.getAllAccounts().getValue();
        if (accountEntityList != null && getContext() != null) {
            accountSpinnerAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    accountEntityList
            );
        }
        accountSpinner.setAdapter(accountSpinnerAdapter);

        Toolbar toolbar = view.findViewById(R.id.add_balance_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                Date date = checkDate.getDate();
                int accountId = ((AccountExtended) accountSpinner.getAdapter().getItem(
                        accountSpinner.getSelectedItemPosition())).id;
                String valueString = valueField.getText().toString();

                if (!TextUtils.isEmpty(valueString)) {
                    double value = Double.parseDouble(valueString);
                    BalanceEntity balance = new BalanceEntity(
                            0, // not set
                            date,
                            accountId,
                            value,
                            true
                    );
                    balanceViewModel.insert(balance);
                    dismiss();
                } else {
                    valueField.setError(getString(R.string.error_value_empty));
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
