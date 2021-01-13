package com.mancode.financetracker.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.pojos.BalanceExtended
import com.mancode.financetracker.ui.BalanceInputView
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.utils.InjectorUtils
import com.mancode.financetracker.viewmodel.AddBalancesViewModel
import com.mancode.financetracker.workers.runUpdateWorker
import kotlinx.android.synthetic.main.fragment_add_balance.*
import org.threeten.bp.LocalDate

private val ViewGroup.views: List<View>
    get() = (0 until childCount).map { getChildAt(it) }

class AddBalanceFragment : Fragment() {

    lateinit var navController: NavController

    private val args: AddBalanceFragmentArgs by navArgs()
    private val viewModel: AddBalancesViewModel by viewModels {
        InjectorUtils.provideAddBalancesViewModelFactory(requireContext(), args.balanceDate
                ?: LocalDate.now())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_balance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        viewModel.accounts.observe(viewLifecycleOwner, { accounts ->
            for (account in accounts) {
                val balanceView = BalanceInputView(requireContext())
                balanceView.setAccount(account)
                container.addView(balanceView)
                if (viewModel.balances.value != null) balanceView.updateWidget(viewModel.balances.value!!)
            }
        })

        viewModel.balances.observe(viewLifecycleOwner, { balances ->
            if (container.childCount > 0) {
                for (balanceView in container.views) {
                    (balanceView as BalanceInputView).updateWidget(balances)
                }
            }
        })

        selectAll.setOnCheckedChangeListener { _, isChecked ->
            for (balanceView in container.views) {
                (balanceView as BalanceInputView).setActive(isChecked)
            }
        }
        if (args.balanceDate == null) selectAll.isChecked = true
        balanceDate.date = viewModel.date
        balanceDate.isEnabled = false

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_menu_save) {

                var noneActive = true
                val accountIds = mutableListOf<Int>()
                for (balanceView in container.views) {
                    val balanceInputView = balanceView as BalanceInputView
                    if (!balanceInputView.isActive() && !balanceInputView.deleted) {
                        continue
                    }
                    noneActive = false
                    val balance = BalanceEntity(
                            balanceInputView.balanceId ?: 0,
                            viewModel.date,
                            balanceInputView.getAccountId(),
                            balanceInputView.getValue(),
                            true
                    )
                    if (balanceInputView.deleted) {
                        viewModel.removeBalance(balance)
                        continue
                    } else {
                        viewModel.addEditBalance(balance)
                    }
                    accountIds.add(balance.accountId)
                }

                if (container.childCount == 0 || noneActive) {
                    Toast.makeText(activity, getString(R.string.error_no_accounts_selected), Toast.LENGTH_SHORT).show()
                } else {
                    requireContext().runUpdateWorker(accountIds.toIntArray(), viewModel.date)
                    dismiss()
                }
            }
            false
        }
        toolbar.inflateMenu(R.menu.menu_dialog)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
    }

    private fun dismiss() {
        hideKeyboard()
        navController.navigateUp()
    }
}

private fun BalanceInputView.updateWidget(balances: List<BalanceExtended>) {
    for (balance in balances) {
        if (getAccountId() == balance.accountId) {
            if (balance.fixed) {
                setValue(balance.value) // TODO needs validation?
                setExisting()
            } else {
                setHint(balance.value)
            }
            balanceId = balance.id
            break
        }
    }
}
