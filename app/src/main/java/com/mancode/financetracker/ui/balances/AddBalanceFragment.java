package com.mancode.financetracker.ui.balances;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.textfield.TextInputLayout;
import com.mancode.financetracker.AddItemFragment;
import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.database.viewmodel.BalanceViewModel;
import com.mancode.financetracker.database.views.AccountExtended;
import com.mancode.financetracker.database.workers.UpdateStateWorker;
import com.mancode.financetracker.ui.SetDateView;

import org.threeten.bp.LocalDate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Manveru on 07.09.2017.
 */

public class AddBalanceFragment extends AddItemFragment {

    private SetDateView checkDate;
    private AutoCompleteTextView accountDropdown;
    private EditText valueField;

    private Map<String, Integer> accountsMap;
    private ArrayAdapter<String> accountDropdownAdapter;

    // TODO refactor to 1 ViewModel
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
        accountDropdown = view.findViewById(R.id.dropdownBalanceAccount);
        valueField = view.findViewById(R.id.tf_balance);
        TextInputLayout valueInputLayout = view.findViewById(R.id.balance_value_input_layout);
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
                    valueInputLayout.setError(getString(R.string.error_field_empty));
                } else {
                    valueInputLayout.setError(null);
                }
            }
        });

        List<AccountExtended> accountEntityList = accountViewModel.getAllAccounts().getValue();
        if (accountEntityList != null && getContext() != null) {
            accountsMap = new LinkedHashMap<>();
            for (AccountExtended account : accountEntityList) {
                accountsMap.put(account.accountName, account.id);
            }
            accountDropdownAdapter = new ArrayAdapter<>(
                    getContext(),
                    R.layout.dropdown_menu_popup_item,
                    accountsMap.keySet().toArray(new String[0])
            );
        }
        accountDropdown.setAdapter(accountDropdownAdapter);
        accountDropdown.setText(accountDropdownAdapter.getItem(0), false);

        Toolbar toolbar = view.findViewById(R.id.add_balance_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                LocalDate date = checkDate.getDate();
                Integer accountId = accountsMap.get(accountDropdown.getText().toString());
                String valueString = valueField.getText().toString();

                if (!TextUtils.isEmpty(valueString) && accountId != null) {
                    double value = Double.parseDouble(valueString);
                    BalanceEntity balance = new BalanceEntity(
                            0, // not set
                            date,
                            accountId,
                            value,
                            true
                    );
                    balanceViewModel.insert(balance);
                    OneTimeWorkRequest request =
                            new OneTimeWorkRequest.Builder(UpdateStateWorker.class).build();
                    WorkManager.getInstance().enqueue(request);
                    dismiss();
                } else if (TextUtils.isEmpty(valueString)) {
                    valueInputLayout.setError(getString(R.string.error_field_empty));
                } else throw new IllegalStateException("accountId is null!");
            }
            return false;
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        return view;
    }
}
