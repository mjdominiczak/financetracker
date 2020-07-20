package com.mancode.financetracker.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.pojos.BalanceExtended
import com.mancode.financetracker.ui.BalanceInputView
import com.mancode.financetracker.ui.hideKeyboard
import com.mancode.financetracker.utils.InjectorUtils
import com.mancode.financetracker.viewmodel.AddBalancesViewModel
import com.mancode.financetracker.workers.UpdateStateWorker
import kotlinx.android.synthetic.main.fragment_add_balance.*
import org.threeten.bp.LocalDate

private val ViewGroup.views: List<View>
    get() = (0 until childCount).map { getChildAt(it) }

class AddBalanceFragment : Fragment() {

    lateinit var navController: NavController

    private val args: AddBalanceFragmentArgs by navArgs()
    val date: LocalDate by lazy { args.balanceDate ?: LocalDate.now() }

    private val viewModel: AddBalancesViewModel by viewModels {
        InjectorUtils.provideAddBalancesViewModelFactory(requireContext(), date)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_balance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        viewModel.accounts.observe(viewLifecycleOwner, Observer { accounts ->
            for (account in accounts) {
                val balanceView = BalanceInputView(requireContext())
                balanceView.setAccount(account)
                container.addView(balanceView)
                if (viewModel.balances.value != null) updateBalanceWidget(balanceView, viewModel.balances.value!!)
            }
        })

        viewModel.balances.observe(viewLifecycleOwner, Observer { balances ->
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
                    WorkManager.getInstance(requireContext()).enqueue(request)
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
            balanceView.setExisting()
        }
    }

    private fun dismiss() {
        hideKeyboard()
        navController.navigateUp()
    }
}
