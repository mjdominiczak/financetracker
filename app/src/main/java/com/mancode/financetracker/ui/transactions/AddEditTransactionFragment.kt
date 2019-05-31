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
import com.mancode.financetracker.database.viewmodel.AddEditTransactionViewModel
import kotlinx.android.synthetic.main.edit_transaction.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class AddEditTransactionFragment : AddItemFragment() {

    /**
     * id not set if adding new transaction
     */
    private var argId: Int = 0
    private var argDate: LocalDate? = null
    private var argType: Int? = null
    private var argDescription: String? = null
    private var argValue: Double? = null
    private var argAccountId: Int? = null
    private var argCategoryId: Int? = null

    private val incomeCategories by lazy { ArrayList<CategoryEntity>() }
    private val outcomeCategories by lazy { ArrayList<CategoryEntity>() }
    private val accountsNamesAndIds by lazy { ArrayList<AccountMini>() }
    private val incomeSpinnerAdapter by lazy { obtainIncomeAdapter() }
    private val outcomeSpinnerAdapter by lazy { obtainOutcomeAdapter() }
    private val accountSpinnerAdapter by lazy { obtainAccountAdapter() }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AddEditTransactionViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AsyncTask.execute {
            incomeCategories.addAll(viewModel.incomeCategories)
            outcomeCategories.addAll(viewModel.outcomeCategories)
            accountsNamesAndIds.addAll(viewModel.accountsNamesAndIds)
        }

        argId = arguments?.getInt(ARG_ID) ?: 0
        if (arguments != null) {
            argDate = LocalDate.parse(arguments?.getString(ARG_DATE))
        }
        argType = arguments?.getInt(ARG_TYPE)
        argDescription = arguments?.getString(ARG_DESCRIPTION)
        argValue = arguments?.getDouble(ARG_VALUE)
        argAccountId = arguments?.getInt(ARG_ACCOUNT_ID)
        argCategoryId = arguments?.getInt(ARG_CATEGORY_ID)
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
                    descriptionInputLayout.error = getString(R.string.error_description_empty)
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
                if (TextUtils.isEmpty(s))
                    valueInputLayout.error = getString(R.string.error_value_empty)
                else
                    valueInputLayout.error = null
            }
        })
        accountSpinner.adapter = accountSpinnerAdapter
        categorySpinner.adapter = outcomeSpinnerAdapter

        if (arguments != null) {
            transactionDate.date = argDate
            if (argType == 1) {
                radioGroupType.check(R.id.rb_income)
            } else {
                radioGroupType.check(R.id.rb_outcome)
            }
            descriptionField.setText(argDescription)
            valueField.setText(argValue.toString())
            val accountIndex = accountsNamesAndIds.map { it.id }.indexOf(argAccountId)
            accountSpinner.setSelection(accountIndex)
            val categoryIndex = if (argType == 1) {
                incomeCategories.map { it.id }.indexOf(argCategoryId)
            } else {
                outcomeCategories.map { it.id }.indexOf(argCategoryId)
            }
            categorySpinner.setSelection(categoryIndex)
        }

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
                            argId,
                            date,
                            type,
                            description,
                            value,
                            account,
                            category
                    )
                    if (argId == 0) {
                        viewModel.insertTransaction(transaction)
                    } else {
                        viewModel.updateTransaction(transaction)
                    }
                    dismiss()
                } else {
                    if (description.isEmpty()) {
                        descriptionInputLayout.error = getString(R.string.error_description_empty)
                    }
                    if (valueString.isEmpty()) {
                        valueInputLayout.error = getString(R.string.error_value_empty)
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

        private const val ARG_ID = "id"
        private const val ARG_DATE = "date"
        private const val ARG_TYPE = "type"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_VALUE = "value"
        private const val ARG_ACCOUNT_ID = "account_id"
        private const val ARG_CATEGORY_ID = "category_id"

        fun newInstance(transaction: TransactionEntity) =
                AddEditTransactionFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ID, transaction.id)
                        putString(ARG_DATE, DateTimeFormatter.ISO_LOCAL_DATE.format(transaction.date))
                        putInt(ARG_TYPE, transaction.type)
                        putString(ARG_DESCRIPTION, transaction.description)
                        putDouble(ARG_VALUE, transaction.value)
                        putInt(ARG_ACCOUNT_ID, transaction.accountId)
                        putInt(ARG_CATEGORY_ID, transaction.categoryId)
                    }
                }
    }
}
