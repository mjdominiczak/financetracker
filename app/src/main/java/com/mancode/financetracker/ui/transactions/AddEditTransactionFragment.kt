package com.mancode.financetracker.ui.transactions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_INCOME
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_OUTCOME
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.viewmodel.AddEditTransactionViewModel
import kotlinx.android.synthetic.main.edit_transaction.*

class AddEditTransactionFragment : Fragment() {

    private val viewModel: AddEditTransactionViewModel by viewModels()
    private val args: AddEditTransactionFragmentArgs by navArgs()

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        viewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.splitCategories()
            if (args.transactionId == 0) {
                updateCategoryAdapter()
            } else if (viewModel.transaction != null) {
                updateCategoryAdapter(false)
            }
        })
        viewModel.accountsLiveData.observe(viewLifecycleOwner, Observer {
            if (args.transactionId == 0) {
                updateAccountAdapter()
            } else if (viewModel.transaction != null) {
                updateAccountAdapter(false)
            }
        })

        if (args.transactionId != 0) {
            viewModel.getTransaction(args.transactionId).observe(viewLifecycleOwner, Observer {
                viewModel.transaction = it
                transactionDate.date = it.date
                if (it.type == TYPE_INCOME) {
                    radioGroupType.check(R.id.rb_income)
                } else {
                    radioGroupType.check(R.id.rb_outcome)
                }
                descriptionField.setText(it.description)
                valueField.setText(it.value.toString())
                accountDropdown.setText(viewModel.getAccountName(), false)
                categoryDropdown.setText(viewModel.getCategoryName(), false)
                if (viewModel.accountsLiveData.value != null) {
                    updateAccountAdapter(false)
                }
                if (viewModel.categoriesLiveData.value != null) {
                    updateCategoryAdapter(false)
                }
            })
        }

        radioGroupType.setOnCheckedChangeListener { _, _ -> updateCategoryAdapter() }
        transactionDate.addDateSetListener { updateAccountAdapter(false) }
        descriptionField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty())
                    descriptionInputLayout.error = getString(R.string.error_field_empty)
                else
                    descriptionInputLayout.error = null
            }
        })
        valueField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty())
                    valueInputLayout.error = getString(R.string.error_field_empty)
                else
                    valueInputLayout.error = null
            }
        })
        with(accountDropdown) {
            setOnClickListener {
                listSelection = viewModel.getAccountIndex(text.toString())
            }
            setOnItemClickListener { _, _, position, _ ->
                viewModel.account = adapter.getItem(position) as AccountEntity
                accountInputLayout.error = null
            }
        }
        with(categoryDropdown) {
            setOnClickListener {
                listSelection = if (radioGroupType.checkedRadioButtonId == R.id.rb_income)
                    viewModel.getIncomeCategoryIndex(text.toString()) else
                    viewModel.getOutcomeCategoryIndex(text.toString())
            }
            setOnItemClickListener { _, _, position, _ ->
                viewModel.category = adapter.getItem(position) as CategoryEntity
            }
        }

        val toolbar = view.findViewById<Toolbar>(R.id.add_transaction_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_menu_save) {
                val description = descriptionField.text.toString()
                val valueString = valueField.text.toString()
                val accountString = accountDropdown.text.toString()
                val categoryString = categoryDropdown.text.toString()

                if (description.isNotEmpty()
                        && valueString.isNotEmpty()
                        && accountString.isNotEmpty()
                        && accountDropdown.error == null
                        && categoryString.isNotEmpty()) {
                    val date = transactionDate.date
                    val type = if (radioGroupType.checkedRadioButtonId == R.id.rb_income)
                        TYPE_INCOME else
                        TYPE_OUTCOME
                    val account = viewModel.getAccountIdByName(accountString)
                    val category = viewModel.category?.id ?: viewModel.transaction!!.categoryId
                    val value = valueString.toDouble()
                    val flags = if (args.transactionId == 0) 0 else viewModel.transaction?.flags ?: 0
                    val transaction = TransactionEntity(
                            args.transactionId,
                            date,
                            type,
                            description,
                            value,
                            flags,
                            account,
                            category
                    )
                    if (args.transactionId == 0) {
                        viewModel.insertTransaction(transaction)
                    } else {
                        viewModel.updateTransaction(transaction)
                    }
                    dismiss()
                } else {
                    if (description.isEmpty()) {
                        descriptionInputLayout.error = getString(R.string.error_field_empty)
                    }
                    if (valueString.isEmpty()) {
                        valueInputLayout.error = getString(R.string.error_field_empty)
                    }
                    if (accountString.isEmpty()) {
                        accountInputLayout.error = getString(R.string.error_field_empty)
                    }
                    if (categoryString.isEmpty()) {
                        categoryInputLayout.error = getString(R.string.error_field_empty)
                    }
                }
            }
            false
        }
        toolbar.inflateMenu(R.menu.menu_dialog)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
    }

    private fun updateAccountAdapter(shouldResetSelection: Boolean = true) {
        with(accountDropdown) {
            setAdapter(obtainAccountAdapter())
            if (viewModel.account != null) {
                accountInputLayout.error = if (!viewModel.isAccountOpenOn(transactionDate.date)) {
                    getString(R.string.error_account_not_open)
                } else if (viewModel.isAccountClosedBefore(transactionDate.date)) {
                    getString(R.string.error_account_closed)
                } else null
            }
            if (shouldResetSelection && !adapter.isEmpty) {
                viewModel.account = adapter.getItem(0) as AccountEntity
            }
            setText(viewModel.getAccountName(), false)
        }
    }

    private fun updateCategoryAdapter(shouldResetSelection: Boolean = true) {
        with(categoryDropdown) {
            when (radioGroupType.checkedRadioButtonId) {
                R.id.rb_income -> {
                    setAdapter(obtainIncomeAdapter())
                }
                R.id.rb_outcome -> {
                    setAdapter(obtainOutcomeAdapter())
                }
            }
            if (shouldResetSelection && !adapter.isEmpty) {
                viewModel.category = adapter.getItem(0) as CategoryEntity
            }
            setText(viewModel.getCategoryName(), false)
        }
    }

    private fun obtainAccountAdapter(): ArrayAdapter<AccountEntity> {
        return ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                viewModel.getAccountsOnDate(transactionDate.date)
        )
    }

    private fun obtainIncomeAdapter(): ArrayAdapter<CategoryEntity> {
        return ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                viewModel.incomeCategories
        )
    }

    private fun obtainOutcomeAdapter(): ArrayAdapter<CategoryEntity> {
        return ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                viewModel.outcomeCategories
        )
    }

    private fun dismiss() {
        hideKeyboard()
        navController.navigateUp()
    }
}
