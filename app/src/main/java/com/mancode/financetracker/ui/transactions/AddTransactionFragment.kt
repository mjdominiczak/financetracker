package com.mancode.financetracker.ui.transactions

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.mancode.financetracker.AddItemFragment
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.AccountMini
import com.mancode.financetracker.database.viewmodel.AddTransactionViewModel
import kotlinx.android.synthetic.main.edit_transaction.*
import java.util.*

class AddTransactionFragment : AddItemFragment() {

    private val incomeCategories by lazy { ArrayList<CategoryEntity>() }
    private val outcomeCategories by lazy { ArrayList<CategoryEntity>() }
    private val accountsNamesAndIds by lazy { ArrayList<AccountMini>() }
    private val incomeSpinnerAdapter by lazy { obtainIncomeAdapter() }
    private val outcomeSpinnerAdapter by lazy { obtainOutcomeAdapter() }
    private val accountSpinnerAdapter by lazy { obtainAccountAdapter() }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AddTransactionViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AsyncTask.execute {
            incomeCategories.addAll(viewModel.incomeCategories)
            outcomeCategories.addAll(viewModel.outcomeCategories)
            accountsNamesAndIds.addAll(viewModel.accountsNamesAndIds)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_income -> categorySpinner.adapter = incomeSpinnerAdapter
                R.id.rb_outcome -> categorySpinner.adapter = outcomeSpinnerAdapter
            }
        }
        descriptionField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s))
                    descriptionField.error = getString(R.string.error_description_empty)
            }
        })
        valueField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s))
                    valueField.error = getString(R.string.error_value_empty)
            }
        })
        accountSpinner.adapter = accountSpinnerAdapter
        categorySpinner.adapter = outcomeSpinnerAdapter

        val toolbar = view.findViewById<Toolbar>(R.id.add_transaction_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_menu_save) {
                val description = descriptionField.text.toString()
                val valueString = valueField.text.toString()
                val date = transactionDate.date
                val type = if (radioGroupType.checkedRadioButtonId == R.id.rb_income) 1 else -1
                val account = (accountSpinner.adapter.getItem(
                        accountSpinner.selectedItemPosition) as AccountMini).id
                val category = (categorySpinner.adapter.getItem(
                        categorySpinner.selectedItemPosition) as CategoryEntity).id

                if (description.isNotEmpty() && valueString.isNotEmpty()) {
                    val value = valueString.toDouble()
                    val transaction = TransactionEntity(
                            0, // not set
                            date,
                            type,
                            description,
                            value,
                            account,
                            category
                    )
                    viewModel.insertTransaction(transaction)
                    dismiss()
                } else {
                    if (description.isEmpty()) {
                        descriptionField.error = getString(R.string.error_description_empty)
                    }
                    if (valueString.isEmpty()) {
                        valueField.error = getString(R.string.error_value_empty)
                    }
                }
            }
            false
        }
        toolbar.inflateMenu(R.menu.menu_dialog)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
    }

    private fun obtainAccountAdapter(): ArrayAdapter<AccountMini>? {
        return if (context == null)
            null
        else
            ArrayAdapter(
                    context as Context,
                    android.R.layout.simple_spinner_dropdown_item,
                    accountsNamesAndIds
            )
    }

    private fun obtainIncomeAdapter(): ArrayAdapter<CategoryEntity>? {
        return if (context == null)
            null
        else
            ArrayAdapter(
                    context as Context,
                    android.R.layout.simple_spinner_dropdown_item,
                    incomeCategories
            )
    }

    private fun obtainOutcomeAdapter(): ArrayAdapter<CategoryEntity>? {
        return if (context == null)
            null
        else
            ArrayAdapter(
                    context as Context,
                    android.R.layout.simple_spinner_dropdown_item,
                    outcomeCategories
            )
    }

    companion object {

        fun newInstance(): AddTransactionFragment {
            return AddTransactionFragment()
        }
    }
}
