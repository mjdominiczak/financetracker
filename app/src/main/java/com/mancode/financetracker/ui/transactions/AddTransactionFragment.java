package com.mancode.financetracker.ui.transactions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.mancode.financetracker.AddItemFragment;
import com.mancode.financetracker.R;
import com.mancode.financetracker.database.entity.CategoryEntity;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.database.viewmodel.CategoryViewModel;
import com.mancode.financetracker.database.viewmodel.TransactionViewModel;
import com.mancode.financetracker.database.views.AccountExtended;
import com.mancode.financetracker.ui.SetDateView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

public class AddTransactionFragment extends AddItemFragment {

    private SetDateView transactionDate;
    private RadioGroup radioGroupType;
    private EditText descriptionField;
    private EditText valueField;
    private Spinner accountSpinner;
    private Spinner categorySpinner;

    private ArrayAdapter<AccountExtended> accountSpinnerAdapter;
    private ArrayAdapter<CategoryEntity> incomeSpinnerAdapter;
    private ArrayAdapter<CategoryEntity> outcomeSpinnerAdapter;
    private List<CategoryEntity> incomeCategories;
    private List<CategoryEntity> outcomeCategories;

    private AccountViewModel accountViewModel;
    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;

    public AddTransactionFragment() { }

    static AddTransactionFragment newInstance() {
        return new AddTransactionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        incomeCategories = new ArrayList<>();
        outcomeCategories = new ArrayList<>();
        if (activity != null) {
            accountViewModel = ViewModelProviders.of(activity).get(AccountViewModel.class);
            categoryViewModel = ViewModelProviders.of(activity).get(CategoryViewModel.class);
            transactionViewModel = ViewModelProviders.of(activity).get(TransactionViewModel.class);
            AsyncTask.execute(() -> {
                incomeCategories.addAll(categoryViewModel.getIncomeCategories());
                outcomeCategories.addAll(categoryViewModel.getOutcomeCategories());
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        transactionDate = view.findViewById(R.id.sd_transaction_date);
        radioGroupType = view.findViewById(R.id.rg_transaction_type);
        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_income:
                    if (incomeSpinnerAdapter == null) {
                        incomeSpinnerAdapter = getIncomeAdapter();
                    }
                    categorySpinner.setAdapter(incomeSpinnerAdapter);
                    break;
                case R.id.rb_outcome:
                    categorySpinner.setAdapter(outcomeSpinnerAdapter);
                    break;
            }
        });
        descriptionField = view.findViewById(R.id.tf_description);
        descriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s))
                    descriptionField.setError(getString(R.string.error_description_empty));
            }
        });
        valueField = view.findViewById(R.id.tf_value);
        valueField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s))
                    valueField.setError(getString(R.string.error_value_empty));
            }
        });
        accountSpinner = view.findViewById(R.id.spinner_transaction_account);
        accountSpinnerAdapter = getAccountAdapter();
        accountSpinner.setAdapter(accountSpinnerAdapter);
        categorySpinner = view.findViewById(R.id.spinner_transaction_category);
        outcomeSpinnerAdapter = getOutcomeAdapter();
        categorySpinner.setAdapter(outcomeSpinnerAdapter);

        Toolbar toolbar = view.findViewById(R.id.add_transaction_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                String description = descriptionField.getText().toString();
                String valueString = valueField.getText().toString();
                Date date = transactionDate.getDate();
                int type = radioGroupType.getCheckedRadioButtonId() == R.id.rb_income ?
                        1 : -1;
                int account = ((AccountExtended) accountSpinner.getAdapter().getItem(
                        accountSpinner.getSelectedItemPosition())).id;
                int category = ((CategoryEntity) categorySpinner.getAdapter().getItem(
                        categorySpinner.getSelectedItemPosition())).id;

                if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(valueString)) {
                    double value = Double.parseDouble(valueString);
                    TransactionEntity transaction = new TransactionEntity(
                            0, // not set
                            date,
                            type,
                            description,
                            value,
                            account,
                            category
                    );
                    transactionViewModel.insertTransaction(transaction);
                    dismiss();
                } else {
                    if (TextUtils.isEmpty(description)) {
                        descriptionField.setError(getString(R.string.error_description_empty));
                    }
                    if (TextUtils.isEmpty(valueString)) {
                        valueField.setError(getString(R.string.error_value_empty));
                    }
                }
            }
            return false;
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        return view;
    }

    private ArrayAdapter<AccountExtended> getAccountAdapter() {
        List<AccountExtended> accountEntityList = accountViewModel.getAllAccounts().getValue();
        return accountEntityList == null || getContext() == null ? null :
                new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    accountEntityList
        );
    }

    private ArrayAdapter<CategoryEntity> getIncomeAdapter() {
        return getContext() == null ? null : new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                incomeCategories
        );
    }

    private ArrayAdapter<CategoryEntity> getOutcomeAdapter() {
        return getContext() == null ? null : new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                outcomeCategories
        );
    }
}
