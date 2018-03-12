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
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.entity.CategoryEntity;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.viewmodel.AccountViewModel;
import com.mancode.financetracker.database.viewmodel.CategoryViewModel;
import com.mancode.financetracker.database.viewmodel.TransactionViewModel;
import com.mancode.financetracker.ui.SetDateView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddTransactionFragment extends AddItemFragment {

    private SetDateView mTransactionDate;
    private RadioGroup mRadioGroupType;
    private EditText mDescriptionField;
    private EditText mValueField;
    private Spinner mAccountSpinner;
    private Spinner mCategorySpinner;

    private ArrayAdapter<AccountEntity> mAccountSpinnerAdapter;
    private ArrayAdapter<CategoryEntity> mCategorySpinnerAdapter;

    private AccountViewModel mAccountViewModel;
    private CategoryViewModel mCategoryViewModel;
    private TransactionViewModel mTransactionViewModel;

    public AddTransactionFragment() { }

    public static AddTransactionFragment newInstance() {
        return new AddTransactionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountViewModel = ViewModelProviders.of(getActivity()).get(AccountViewModel.class);
        mCategoryViewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        mCategoryViewModel.getAllCategories().observe(this, categories -> {
            if (mCategorySpinnerAdapter != null) {
                mCategorySpinnerAdapter.clear();
                List<CategoryEntity> temp = mCategoryViewModel.getAllCategories().getValue();
                if (temp != null) {
                    mCategorySpinnerAdapter.addAll(temp);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        mTransactionDate = view.findViewById(R.id.sd_transaction_date);
        mRadioGroupType = view.findViewById(R.id.rg_transaction_type);
        mDescriptionField = view.findViewById(R.id.tf_description);
        mValueField = view.findViewById(R.id.tf_value);
        mAccountSpinner = view.findViewById(R.id.spinner_transaction_account);
        mAccountSpinnerAdapter = getAccountAdapter();
        mAccountSpinner.setAdapter(mAccountSpinnerAdapter);
        mCategorySpinner = view.findViewById(R.id.spinner_transaction_category);
        mCategorySpinnerAdapter = getCategoryAdapter();
        mCategorySpinner.setAdapter(mCategorySpinnerAdapter);

        Toolbar toolbar = view.findViewById(R.id.add_transaction_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                Date date = mTransactionDate.getDate();
                int type = mRadioGroupType.getCheckedRadioButtonId() == R.id.rb_income ?
                        1 : -1;
                String description = mDescriptionField.getText().toString();
                double value = Double.parseDouble(mValueField.getText().toString());
                int account = mAccountSpinnerAdapter.getItem(
                        mAccountSpinner.getSelectedItemPosition()).getId();
                int category = mCategorySpinnerAdapter.getItem(
                        mCategorySpinner.getSelectedItemPosition()).getId();
                TransactionEntity transaction = new TransactionEntity(
                        0, // not set
                        date,
                        type,
                        description,
                        value,
                        account,
                        category
                );

                if (true) { // TODO validate
                    mTransactionViewModel.insertTransaction(transaction);
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

    private ArrayAdapter<AccountEntity> getAccountAdapter() {
        List<AccountEntity> accountEntityList = mAccountViewModel.getAllAccounts().getValue();
        return accountEntityList == null ? null : new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                accountEntityList
        );
    }

    private ArrayAdapter<CategoryEntity> getCategoryAdapter() {
        return new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>()
        );
    }
}
