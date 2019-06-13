package com.mancode.financetracker.ui.balances

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mancode.financetracker.AddItemFragment
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.viewmodel.AccountViewModel
import com.mancode.financetracker.viewmodel.BalanceViewModel
import com.mancode.financetracker.workers.UpdateStateWorker
import kotlinx.android.synthetic.main.edit_balance.*
import kotlinx.android.synthetic.main.fragment_add_balance.*

class AddBalanceFragment : AddItemFragment() {

    private val accountsMap by lazy { LinkedHashMap<String, Int>() }
    private val accountDropdownAdapter by lazy { obtainAccountAdapter() }

    // TODO refactor to 1 ViewModel
    private val accountViewModel by lazy {
        ViewModelProviders.of(this).get(AccountViewModel::class.java)
    }
    private val balanceViewModel by lazy {
        ViewModelProviders.of(this).get(BalanceViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_balance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        balanceValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    balanceValueInputLayout.error = getString(R.string.error_field_empty)
                } else {
                    balanceValueInputLayout.error = null
                }
            }
        })

        accountDropdown.setAdapter<ArrayAdapter<String>>(accountDropdownAdapter)
        accountDropdown.setText(accountDropdownAdapter.getItem(0), false)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_menu_save) {
                val date = checkDate.date
                val accountId = accountsMap[accountDropdown.text.toString()]
                val valueString = balanceValue.text.toString()

                if (valueString.isNotEmpty() && accountId != null) {
                    val value = valueString.toDouble()
                    val balance = BalanceEntity(
                            0, // not set
                            date,
                            accountId,
                            value,
                            true
                    )
                    balanceViewModel.insert(balance)
                    val request = OneTimeWorkRequest.Builder(UpdateStateWorker::class.java).build()
                    WorkManager.getInstance().enqueue(request)
                    dismiss()
                } else if (valueString.isEmpty()) {
                    balanceValueInputLayout.error = getString(R.string.error_field_empty)
                } else
                    throw IllegalStateException("accountId is null!")
            }
            false
        }
        toolbar.inflateMenu(R.menu.menu_dialog)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)

    }

    private fun obtainAccountAdapter(): ArrayAdapter<String> {
        val accountEntityList = accountViewModel.allAccounts.value
        if (accountEntityList != null && context != null) {
            for (account in accountEntityList) {
                accountsMap[account.accountName] = account.id
            }
        }
        return ArrayAdapter(
                context
                        ?: throw IllegalStateException("Context null when trying to obtain adapter"),
                R.layout.dropdown_menu_popup_item,
                accountsMap.keys.toTypedArray()
        )
    }

    companion object {

        internal fun newInstance(): AddBalanceFragment {
            return AddBalanceFragment()
        }
    }
}
