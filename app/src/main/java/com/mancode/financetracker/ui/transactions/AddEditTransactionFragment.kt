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
import androidx.lifecycle.ViewModelProviders
import com.mancode.financetracker.AddItemFragment
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.entity.TransactionEntity.TYPE_INCOME
import com.mancode.financetracker.database.entity.TransactionEntity.TYPE_OUTCOME
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

    private val categories by lazy { ArrayList<CategoryEntity>() }
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

        if (arguments != null) {
            transactionDate.date = argDate
            if (argType == TYPE_INCOME) {
                radioGroupType.check(R.id.rb_income)
            } else {
                radioGroupType.check(R.id.rb_outcome)
            }
            descriptionField.setText(argDescription)
            valueField.setText(argValue.toString())
            val accountIndex = accountsNamesAndIds.map { it.id }.indexOf(argAccountId)
            accountDropdown.setText(accountsNamesAndIds[accountIndex].accountName, false)
            val categoryIndex = if (argType == TYPE_INCOME) {
                incomeCategories.map { it.id }.indexOf(argCategoryId)
            } else {
                outcomeCategories.map { it.id }.indexOf(argCategoryId)
            }
            categoryDropdown.setText(categories[categoryIndex].category, false)
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
                    val category = categories[if (type == TYPE_INCOME) {
                        incomeCategories.map { it.category }.indexOf(categoryString)
                    } else {
                        outcomeCategories.map { it.category }.indexOf(categoryString)
                    }].id
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
