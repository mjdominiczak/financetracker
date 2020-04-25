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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.ui.SetDateView;
import com.mancode.financetracker.ui.UIUtilsKt;
import com.mancode.financetracker.ui.prefs.PreferenceAccessor;
import com.mancode.financetracker.viewmodel.AccountViewModel;

import org.joda.money.CurrencyUnit;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class AddAccountFragment extends Fragment {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);

        nameField = view.findViewById(R.id.tf_account_name);
        TextInputLayout nameInputLayout = view.findViewById(R.id.account_name_input_layout);
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
                    nameInputLayout.setError(getString(R.string.error_field_empty));
                } else {
                    nameInputLayout.setError(null);
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
            availableCurrencyCodes = currencyCodesList.toArray(new String[0]);
            currencyAdapter = new ArrayAdapter<>(
                    getContext(),
                    R.layout.dropdown_menu_popup_item,
                    availableCurrencyCodes
            );
            dropdownCurrency.setAdapter(currencyAdapter);
            dropdownCurrency.setText(defaultCurrency, false);
            dropdownCurrency.setOnClickListener(v -> {
                int preselectionIndex = currencyCodesList.indexOf(dropdownCurrency.getText().toString());
                dropdownCurrency.setListSelection(preselectionIndex);
            });
        }
        checkBoxClosed.setOnClickListener(v -> closeDate.setEnabled(checkBoxClosed.isChecked()));
        closeDate.setEnabled(false);

        Toolbar toolbar = view.findViewById(R.id.add_account_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                String name = nameField.getText().toString();
                int type = radioGroupType.getCheckedRadioButtonId() == R.id.rb_assets ?
                        AccountEntity.TYPE_ASSETS : AccountEntity.TYPE_LIABILITIES;
                LocalDate openDate = this.openDate.getDate();
                LocalDate closeDate = this.closeDate.getDate();
                String currency = dropdownCurrency.getText().toString();

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
                    nameInputLayout.setError(getString(R.string.error_field_empty));
                }
            }
            return false;
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        return view;
    }

    private void dismiss() {
        UIUtilsKt.hideKeyboard(this);
        NavHostFragment.findNavController(this).
                navigate(R.id.action_addAccountFragment_to_accountFragment);
    }
}
