package com.mancode.financetracker;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.viewmodel.TransactionViewModel;
import com.mancode.financetracker.ui.SetDateView;

import java.util.Date;

public class AddTransactionFragment extends AddItemFragment {

    private SetDateView mTransactionDate;
    private RadioGroup mRadioGroupType;
    private EditText mDescriptionField;
    private EditText mValueField;

    private TransactionViewModel mTransactionViewModel;

    public AddTransactionFragment() { }

    public static AddTransactionFragment newInstance() {
        return new AddTransactionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        mTransactionDate = view.findViewById(R.id.sd_transaction_date);
        mRadioGroupType = view.findViewById(R.id.rg_transaction_type);
        mDescriptionField = view.findViewById(R.id.tf_description);
        mValueField = view.findViewById(R.id.tf_value);

        Toolbar toolbar = view.findViewById(R.id.add_transaction_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu_save) {
                Date date = mTransactionDate.getDate();
                int type = mRadioGroupType.getCheckedRadioButtonId() == R.id.rb_income ?
                        1 : -1;
                String description = mDescriptionField.getText().toString();
                double value = Double.parseDouble(mValueField.getText().toString());
                int account = 7; // TODO hard-coded account
                int category = 1; // TODO hard-coded category
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
}
