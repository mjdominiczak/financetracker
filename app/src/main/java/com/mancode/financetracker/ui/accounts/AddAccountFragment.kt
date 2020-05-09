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
import androidx.navigation.fragment.NavHostFragment
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.ui.prefs.PreferenceAccessor.defaultCurrency
import com.mancode.financetracker.viewmodel.AccountViewModel
import kotlinx.android.synthetic.main.edit_account.*
import kotlinx.android.synthetic.main.fragment_add_account.*
import org.joda.money.CurrencyUnit
import java.util.*

class AddAccountFragment : Fragment() {
    private val accountViewModel: AccountViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val closeDate = accountCloseDate.date
        val currency = dropdownCurrency.text.toString()
        if (name.isNotEmpty()) {
            val account = AccountEntity(
                    0,  // not set
                    name,
                    type,
                    currency,
                    openDate,
                    closeDate
            )
            accountViewModel.insert(account)
            dismiss()
        } else {
            accountNameInputLayout.error = getString(R.string.error_field_empty)
        }
    }

    private fun dismiss() {
        this.hideKeyboard()
        NavHostFragment.findNavController(this).navigate(R.id.action_addAccountFragment_to_accountFragment)
    }
}