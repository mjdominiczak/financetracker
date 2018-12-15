package com.mancode.financetracker.ui.balances;

import android.os.Bundle;
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

    private SetDateView mCheckDate;
    private Spinner mAccountSpinner;
    private EditText mBalanceValue;

    private ArrayAdapter<AccountExtended> mAccountSpinnerAdapter;

    private AccountViewModel mAccountViewModel;
    private BalanceViewModel mBalanceViewModel;

    public AddBalanceFragment() { }

    static AddBalanceFragment newInstance() {
        return new AddBalanceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            mAccountViewModel = ViewModelProviders.of(activity).get(AccountViewModel.class);
            mBalanceViewModel = ViewModelProviders.of(activity).get(BalanceViewModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_balance, container, false);

        mCheckDate = view.findViewById(R.id.tf_balance_check_date);
        mAccountSpinner = view.findViewById(R.id.spinner_balance_account);
        mBalanceValue = view.findViewById(R.id.tf_balance);

        List<AccountExtended> accountEntityList = mAccountViewModel.getAllAccounts().getValue();
        if (accountEntityList != null && getContext() != null) {
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
                        mAccountSpinner.getSelectedItemPosition()).id;
                double value = Double.parseDouble(mBalanceValue.getText().toString());
                BalanceEntity balance = new BalanceEntity(
                        0, // not set
                        checkDate,
                        accountId,
                        value,
                        true
                );

                //noinspection ConstantConditions
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
