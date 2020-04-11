package com.mancode.financetracker.ui.transactions

import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_INCOME
import com.mancode.financetracker.database.entity.TransactionEntity.Companion.TYPE_OUTCOME
import com.mancode.financetracker.database.pojos.AccountMini
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.viewmodel.AddEditTransactionViewModel
import kotlinx.android.synthetic.main.edit_transaction.*

class AddEditTransactionFragment : Fragment() {

    private val args: AddEditTransactionFragmentArgs by navArgs()
    private val transaction: LiveData<TransactionEntity> by lazy {
        viewModel.getTransaction(args.transactionId)
    }

    private val categories by lazy { ArrayList<CategoryEntity>() }
    private val incomeCategories by lazy { ArrayList<CategoryEntity>() }
    private val outcomeCategories by lazy { ArrayList<CategoryEntity>() }
    private val accountsNamesAndIds by lazy { ArrayList<AccountMini>() }
    private val incomeSpinnerAdapter by lazy { obtainIncomeAdapter() }
    private val outcomeSpinnerAdapter by lazy { obtainOutcomeAdapter() }
    private val accountSpinnerAdapter by lazy { obtainAccountAdapter() }

    private lateinit var navController: NavController

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AddEditTransactionViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AsyncTask.execute {
            categories.addAll(viewModel.categories)
            incomeCategories.addAll(categories.filter { it.categoryType == TYPE_INCOME })
            outcomeCategories.addAll(categories.filter { it.categoryType == TYPE_OUTCOME })
            if (categoryDropdown != null && categoryDropdown.text.isEmpty() && categoryDropdown.adapter != null) {
                categoryDropdown.setText(
                        (categoryDropdown.adapter.getItem(0) as CategoryEntity).category,
                        false)
            }
            accountsNamesAndIds.addAll(viewModel.accountsNamesAndIds)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        if (args.transactionId != 0) {
            transaction.observe(viewLifecycleOwner, Observer { transaction ->
                transactionDate.date = transaction.date
                if (transaction.type == TYPE_INCOME) {
                    radioGroupType.check(R.id.rb_income)
                } else {
                    radioGroupType.check(R.id.rb_outcome)
                }
                descriptionField.setText(transaction.description)
                valueField.setText(transaction.value.toString())
                val accountIndex = accountsNamesAndIds.map { it.id }.indexOf(transaction.accountId)
                accountDropdown.setText(accountsNamesAndIds[accountIndex].accountName, false)
                val categoryIndex = categories.map { it.id }.indexOf(transaction.categoryId)
                categoryDropdown.setText(categories[categoryIndex].category, false)
            })
        }

        radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_income -> {
                    with(categoryDropdown) {
                        setAdapter(incomeSpinnerAdapter)
                        if (text.isNotEmpty()) setText(incomeSpinnerAdapter.getItem(0)?.category, false)
                    }
                }
                R.id.rb_outcome -> {
                    with(categoryDropdown) {
                        setAdapter(outcomeSpinnerAdapter)
                        if (text.isNotEmpty()) setText(outcomeSpinnerAdapter.getItem(0)?.category, false)
                    }
                }
            }
        }
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
            setAdapter(accountSpinnerAdapter)
            if (!accountSpinnerAdapter.isEmpty)
                setText((adapter.getItem(0) as AccountMini).accountName, false)
            setOnClickListener {
                listSelection = accountsNamesAndIds.map { it.accountName }.indexOf(text.toString())
            }
        }
        with(categoryDropdown) {
            setAdapter(outcomeSpinnerAdapter)
            if (!adapter.isEmpty)
                setText((adapter.getItem(0) as CategoryEntity).category, false)
            setOnClickListener {
                listSelection =
                        if (radioGroupType.checkedRadioButtonId == R.id.rb_income)
                            incomeCategories.map { it.category }.indexOf(text.toString())
                        else outcomeCategories.map { it.category }.indexOf(text.toString())
            }
        }

        val toolbar = view.findViewById<Toolbar>(R.id.add_transaction_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_menu_save) {
                val description = descriptionField.text.toString()
                val valueString = valueField.text.toString()
                val accountString = accountDropdown.text.toString()
                val categoryString = categoryDropdown.text.toString()

                if (description.isNotEmpty() && valueString.isNotEmpty()
                        && accountString.isNotEmpty() && categoryString.isNotEmpty()) {
                    val date = transactionDate.date
                    val type = if (radioGroupType.checkedRadioButtonId == R.id.rb_income) TYPE_INCOME else TYPE_OUTCOME
                    val account = accountsNamesAndIds[accountsNamesAndIds.map { it.accountName }
                            .indexOf(accountString)].id
                    val category = categories[
                            categories.map { it.category }.indexOf(categoryString)].id
                    val value = valueString.toDouble()
                    val flags = if (args.transactionId == 0) 0 else transaction.value?.flags ?: 0
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

    private fun obtainAccountAdapter(): ArrayAdapter<AccountMini> {
        return ArrayAdapter(
                context
                        ?: throw IllegalStateException("Context null when trying to obtain adapter"),
                R.layout.dropdown_menu_popup_item,
                accountsNamesAndIds
        )
    }

    private fun obtainIncomeAdapter(): ArrayAdapter<CategoryEntity> {
        return ArrayAdapter(
                context
                        ?: throw IllegalStateException("Context null when trying to obtain adapter"),
                R.layout.dropdown_menu_popup_item,
                incomeCategories
        )
    }

    private fun obtainOutcomeAdapter(): ArrayAdapter<CategoryEntity> {
        return ArrayAdapter(
                context
                        ?: throw IllegalStateException("Context null when trying to obtain adapter"),
                R.layout.dropdown_menu_popup_item,
                outcomeCategories
        )
    }

    private fun dismiss() {
        hideKeyboard()
        navController.navigate(R.id.action_addEditTransactionFragment_to_transactionFragment)
    }
}
