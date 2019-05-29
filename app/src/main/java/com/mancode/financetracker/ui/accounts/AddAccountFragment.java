package com.mancode.financetracker.ui.accounts;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.mancode.financetracker.AddItemFragment;
import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.ui.SetDateView;
import com.mancode.financetracker.ui.prefs.PreferenceAccessor;

import org.joda.money.CurrencyUnit;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;


public class AddAccountFragment extends AddItemFragment {

    private EditText nameField;
    private AutoCompleteTextView dropdownCurrency;
    private RadioGroup radioGroupType;
    private SetDateView openDate;
    private SetDateView closeDate;
    private CheckBox checkBoxClosed;

    private ArrayAdapter<String> currencyAdapter;

    private AccountViewModel accountViewModel;

    public AddAccountFragment() {
    }

    static AddAccountFragment newInstance() {
        return new AddAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);

        nameField = view.findViewById(R.id.tf_account_name);
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    nameField.setError(getString(R.string.error_name_empty));
                }
            }
        });
        dropdownCurrency = view.findViewById(R.id.dropdownCurrency);
        radioGroupType = view.findViewById(R.id.rg_account_type);
        openDate = view.findViewById(R.id.sd_account_open_date);
        closeDate = view.findViewById(R.id.sd_account_close_date);
        checkBoxClosed = view.findViewById(R.id.cb_account_closed);

        if (getContext() != null) {
            List<CurrencyUnit> currencies = CurrencyUnit.registeredCurrencies();
            String[] availableCurrencyCodes;
            List<String> currencyCodesList = new ArrayList<>();
            for (CurrencyUnit currency : currencies) {
                currencyCodesList.add(currency.getCode());
            }
            String defaultCurrency = PreferenceAccessor.INSTANCE.getDefaultCurrency();
            int preselectionIndex = currencyCodesList.indexOf(
                    defaultCurrency);
            availableCurrencyCodes = currencyCodesList.toArray(new String[0]);
            currencyAdapter = new ArrayAdapter<>(
                    getContext(),
                    R.layout.dropdown_menu_popup_item,
                    availableCurrencyCodes
            );
            dropdownCurrency.setAdapter(currencyAdapter);
            dropdownCurrency.setText(defaultCurrency, false);
            dropdownCurrency.setOnClickListener(v -> dropdownCurrency.setListSelection(preselectionIndex));
        }
        checkBoxClosed.setOnClickListener(v -> closeDate.setEnabled(checkBoxClosed.isChecked()));
        closeDate.setEnabled(false);

        Toolbar toolbar = view.findViewById(R.id.add_account_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                String name = nameField.getText().toString();
                int type = radioGroupType.getCheckedRadioButtonId() == R.id.rb_assets ?
                        1 : -1;
                LocalDate openDate = this.openDate.getDate();
                LocalDate closeDate = this.closeDate.getDate();
                String currency = currencyAdapter.getItem(dropdownCurrency.getListSelection());

                if (!TextUtils.isEmpty(name)) {
                    AccountEntity account = new AccountEntity(
                            0, // not set
                            name,
                            type,
                            currency,
                            openDate,
                            closeDate
                    );
                    accountViewModel.insert(account);
                    dismiss();
                } else {
                    nameField.setError(getString(R.string.error_name_empty));
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
