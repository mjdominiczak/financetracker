package com.mancode.financetracker.ui.accounts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.ui.prefs.PreferenceAccessor.defaultCurrency
import com.mancode.financetracker.viewmodel.AddEditAccountViewModel
import kotlinx.android.synthetic.main.edit_account.*
import kotlinx.android.synthetic.main.fragment_add_account.*
import org.joda.money.CurrencyUnit

class AddEditAccountFragment : Fragment() {
    private val args: AddEditAccountFragmentArgs by navArgs()
    private val account: LiveData<AccountEntity> by lazy {
        viewModel.getAccount(args.accountId)
    }
    private val viewModel: AddEditAccountViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_account, container, false)
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