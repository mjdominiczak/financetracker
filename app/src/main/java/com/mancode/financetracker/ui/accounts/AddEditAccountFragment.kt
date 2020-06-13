package com.mancode.financetracker.ui.accounts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.ui.SetDateView
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.ui.prefs.PreferenceAccessor.defaultCurrency
import com.mancode.financetracker.viewmodel.AddEditAccountViewModel
import kotlinx.android.synthetic.main.edit_account.*
import kotlinx.android.synthetic.main.fragment_add_account.*
import org.joda.money.CurrencyUnit
import org.threeten.bp.LocalDate

class AddEditAccountFragment : Fragment() {
    private val args: AddEditAccountFragmentArgs by navArgs()
    private val account: LiveData<AccountEntity> by lazy {
        viewModel.getAccount(args.accountId)
    }
    private val viewModel: AddEditAccountViewModel by viewModels()
    private val lastUsageDate: LiveData<LocalDate?> by lazy { viewModel.getLastUsageDate(args.accountId) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_account, container, false)
        if (args.accountId != 0) {
            lastUsageDate.observe(viewLifecycleOwner, Observer {
                if (it == null) {
                    view.findViewById<MaterialButton>(R.id.removeAccount).visibility = View.VISIBLE
                } else {
                    view.findViewById<CheckBox>(R.id.accountClosed).visibility = View.VISIBLE
                    view.findViewById<SetDateView>(R.id.accountCloseDate).visibility = View.VISIBLE
                }
            })
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.accountId != 0) {
            account.observe(viewLifecycleOwner, Observer {
                accountName.setText(it.accountName)
                dropdownCurrency.setText(it.currency)
                currencyInputLayout.isEnabled = false
                accountType.check(
                        if (it.accountType == AccountEntity.TYPE_ASSETS)
                            R.id.assets else
                            R.id.liabilities)
                for (i in 0 until accountType.childCount) {
                    accountType.getChildAt(i).isEnabled = false
                }
                accountOpenDate.date = it.openDate
                accountOpenDate.isEnabled = false
                accountClosed.isChecked = it.closeDate != null
                if (it.closeDate != null) accountCloseDate.date = it.closeDate
            })
        }

        accountName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    accountNameInputLayout.error = getString(R.string.error_field_empty)
                } else {
                    accountNameInputLayout.error = null
                }
            }
        })
        val currencies = CurrencyUnit.registeredCurrencies()
        val currencyCodesList: MutableList<String> = ArrayList()
        for (currency in currencies) {
            currencyCodesList.add(currency.code)
        }
        val defaultCurrency = defaultCurrency
        val availableCurrencyCodes = currencyCodesList.toTypedArray()
        val currencyAdapter = ArrayAdapter(
                context!!,
                R.layout.dropdown_menu_popup_item,
                availableCurrencyCodes
        )
        dropdownCurrency.setAdapter(currencyAdapter)
        dropdownCurrency.setText(defaultCurrency, false)
        dropdownCurrency.setOnClickListener {
            val preselectionIndex = currencyCodesList.indexOf(dropdownCurrency.text.toString())
            dropdownCurrency.listSelection = preselectionIndex
        }
        accountClosed.setOnClickListener { accountCloseDate.isEnabled = accountClosed.isChecked }
        accountCloseDate.isEnabled = false
        accountCloseDate.addDateSetListener {
            if (accountCloseDate.date.isBefore(lastUsageDate.value)) {
                accountCloseDate.date = lastUsageDate.value
                Toast.makeText(context, getString(R.string.warning_account_used), Toast.LENGTH_SHORT)
                        .show()
            }
        }
        removeAccount.setOnClickListener {
            viewModel.remove(account.value!!)
            dismiss()
        }
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.action_menu_save) {
                validateAndSave()
            }
            false
        }
        toolbar.inflateMenu(R.menu.menu_dialog)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
    }

    private fun validateAndSave() {
        val name = accountName.text.toString()
        val type = if (accountType.checkedRadioButtonId == R.id.assets)
            AccountEntity.TYPE_ASSETS else
            AccountEntity.TYPE_LIABILITIES
        val openDate = accountOpenDate.date
        val closeDate = if (accountClosed.isChecked) accountCloseDate.date else null
        val currency = dropdownCurrency.text.toString()
        if (name.isNotEmpty()) {
            val account = AccountEntity(
                    args.accountId,
                    name,
                    type,
                    currency,
                    openDate,
                    closeDate
            )
            if (args.accountId == 0) {
                viewModel.insert(account)
            } else {
                viewModel.update(account)
            }
            dismiss()
        } else {
            accountNameInputLayout.error = getString(R.string.error_field_empty)
        }
    }

    private fun dismiss() {
        this.hideKeyboard()
        NavHostFragment.findNavController(this).navigateUp()
    }
}