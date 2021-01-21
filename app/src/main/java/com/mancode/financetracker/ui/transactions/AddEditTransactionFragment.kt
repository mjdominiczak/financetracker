package com.mancode.financetracker.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_INCOME
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_OUTCOME
import com.mancode.financetracker.databinding.FragmentAddTransactionBinding
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.viewmodel.AddEditTransactionViewModel
import com.mancode.financetracker.workers.runUpdateWorker

class AddEditTransactionFragment : Fragment(R.layout.fragment_add_transaction) {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditTransactionViewModel by viewModels()
    private val args: AddEditTransactionFragmentArgs by navArgs()

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        viewModel.categoriesLiveData.observe(viewLifecycleOwner, {
            viewModel.splitCategories()
            if (args.transactionId == 0) {
                updateCategoryAdapter()
            } else if (viewModel.transaction != null) {
                updateCategoryAdapter(false)
            }
        })
        viewModel.accountsLiveData.observe(viewLifecycleOwner, {
            if (args.transactionId == 0) {
                updateAccountAdapter()
            } else if (viewModel.transaction != null) {
                updateAccountAdapter(false)
            }
        })

        if (args.transactionId != 0) {
            viewModel.getTransaction(args.transactionId).observe(viewLifecycleOwner, {
                viewModel.transaction = it
                with(binding.editTransactionLayout) {
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
                    radioGroupType.setOnCheckedChangeListener { _, _ -> updateCategoryAdapter() }
                }
            })
        }

        with(binding.editTransactionLayout) {
            if (args.transactionId == 0) {
                radioGroupType.setOnCheckedChangeListener { _, _ -> updateCategoryAdapter() }
            }
            transactionDate.addDateSetListener { updateAccountAdapter(false) }
            descriptionField.doAfterTextChanged {
                if (it!!.isEmpty()) descriptionInputLayout.error = getString(R.string.error_field_empty)
                else descriptionInputLayout.error = null
            }
            valueField.doAfterTextChanged {
                if (it!!.isEmpty()) valueInputLayout.error = getString(R.string.error_field_empty)
                else valueInputLayout.error = null
            }
            accountDropdown.setOnClickListener {
                accountDropdown.listSelection = viewModel.getAccountIndex(accountDropdown.text.toString())
            }
            accountDropdown.setOnItemClickListener { _, _, position, _ ->
                viewModel.account = accountDropdown.adapter.getItem(position) as AccountEntity
                accountInputLayout.error = null
            }
            categoryDropdown.setOnClickListener {
                categoryDropdown.listSelection = if (radioGroupType.checkedRadioButtonId == R.id.rb_income)
                    viewModel.getIncomeCategoryIndex(categoryDropdown.text.toString()) else
                    viewModel.getOutcomeCategoryIndex(categoryDropdown.text.toString())
            }
            categoryDropdown.setOnItemClickListener { _, _, position, _ ->
                viewModel.category = categoryDropdown.adapter.getItem(position) as CategoryEntity
            }
        }

        val toolbar = view.findViewById<Toolbar>(R.id.add_transaction_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_menu_save) {
                with(binding.editTransactionLayout) {
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
                        val flags = if (args.transactionId == 0) 0 else
                            viewModel.transaction?.flags ?: 0
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
                        requireContext().runUpdateWorker(intArrayOf(account), date)
                        dismiss()
                    } else {
                        if (description.isEmpty())
                            descriptionInputLayout.error = getString(R.string.error_field_empty)
                        if (valueString.isEmpty())
                            valueInputLayout.error = getString(R.string.error_field_empty)
                        if (accountString.isEmpty())
                            accountInputLayout.error = getString(R.string.error_field_empty)
                        if (categoryString.isEmpty())
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun updateAccountAdapter(shouldResetSelection: Boolean = true) {
        with(binding.editTransactionLayout.accountDropdown) {
            setAdapter(obtainAccountAdapter())
            if (viewModel.account != null) {
                binding.editTransactionLayout.accountInputLayout.error =
                        if (!viewModel.isAccountOpenOn(binding.editTransactionLayout.transactionDate.date)) {
                            getString(R.string.error_account_not_open)
                        } else if (viewModel.isAccountClosedBefore(binding.editTransactionLayout.transactionDate.date)) {
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
        with(binding.editTransactionLayout.categoryDropdown) {
            when (binding.editTransactionLayout.radioGroupType.checkedRadioButtonId) {
                R.id.rb_income -> setAdapter(obtainIncomeAdapter())
                R.id.rb_outcome -> setAdapter(obtainOutcomeAdapter())
            }
            if (shouldResetSelection && !adapter.isEmpty) {
                viewModel.category = adapter.getItem(0) as CategoryEntity
            }
            if (adapter.isEmpty) {
                viewModel.category = null
                viewModel.transaction = null
            }
            setText(viewModel.getCategoryName(), false)
        }
    }

    private fun obtainAccountAdapter(): ArrayAdapter<AccountEntity> {
        return ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                viewModel.getAccountsOnDate(binding.editTransactionLayout.transactionDate.date)
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
