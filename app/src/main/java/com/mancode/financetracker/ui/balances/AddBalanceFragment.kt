package com.mancode.financetracker.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mancode.financetracker.AddItemFragment
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.pojos.BalanceExtended
import com.mancode.financetracker.ui.BalanceInputView
import com.mancode.financetracker.utils.InjectorUtils
import com.mancode.financetracker.viewmodel.AddBalancesViewModel
import com.mancode.financetracker.workers.UpdateStateWorker
import kotlinx.android.synthetic.main.fragment_add_balance.*
import org.threeten.bp.LocalDate

private val ViewGroup.views: List<View>
    get() = (0 until childCount).map { getChildAt(it) }

class AddBalanceFragment(private val date: LocalDate) : AddItemFragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this,
                InjectorUtils.provideAddBalancesViewModelFactory(requireContext(), date))
                .get(AddBalancesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_balance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.accounts.observe(this, Observer { accounts ->
            for (account in accounts) {
                val balanceView = BalanceInputView(requireContext())
                balanceView.setAccount(account)
                container.addView(balanceView)
                if (viewModel.balances.value != null) updateBalanceWidget(balanceView, viewModel.balances.value!!)
            }
        })

        viewModel.balances.observe(this, Observer { balances ->
            if (container.childCount > 0) {
                for (balanceView in container.views) {
                    updateBalanceWidget(balanceView as BalanceInputView, balances)
                }
            }
        })

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_menu_save) {

                var noneActive = true
                for (balanceView in container.views) {
                    val balanceInputView = balanceView as BalanceInputView
                    if (!balanceInputView.isActive()) continue

                    noneActive = false

                    val balance = BalanceEntity(
                            balanceInputView.balanceId ?: 0,
                            viewModel.date,
                            balanceInputView.getAccountId(),
                            balanceInputView.getValue(),
                            true
                    )
                    viewModel.addEditBalance(balance)
                }

                if (container.childCount == 0 || noneActive) {
                    Toast.makeText(activity, "No accounts selected!", Toast.LENGTH_SHORT).show()
                } else {
                    val request = OneTimeWorkRequest.Builder(UpdateStateWorker::class.java).build()
                    WorkManager.getInstance().enqueue(request)
                    dismiss()
                }
            }
            false
        }
        toolbar.inflateMenu(R.menu.menu_dialog)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)

    }

    private fun updateBalanceWidget(balanceView: BalanceInputView, balances: List<BalanceExtended>) {
        var balanceFound = false

        for (balance in balances) {
            if (balanceView.getAccountId() == balance.accountId) {
                balanceView.setValue(balance.value) // TODO needs validation?
                balanceView.balanceId = balance.id
                balanceFound = true
                break
            }
        }
        if (balanceFound) {
            balanceView.setActive(false)
        }
    }

    companion object {

        internal fun newInstance(date: LocalDate): AddBalanceFragment {
            return AddBalanceFragment(date)
        }
    }
}
