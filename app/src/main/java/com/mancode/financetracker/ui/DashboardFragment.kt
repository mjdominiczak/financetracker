package com.mancode.financetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.mancode.financetracker.R
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {

    lateinit var navController: NavController
    private val viewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        navController = findNavController()
        if (PreferenceAccessor.firstRun) navController.navigate(R.id.firstRunFragment)
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.assetsAccountsNumber.observe(viewLifecycleOwner, Observer {
            assets_accounts.text = getAccountsString(it)
        })
        viewModel.liabilitiesAccountsNumber.observe(viewLifecycleOwner, Observer {
            liabilities_accounts.text = getAccountsString(it)
        })
        viewModel.actualNetValue.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                actualNetValue.setFormattedMoney(it)
            } else {
                actualNetValue.text = "---"
            }
        })
        reportMonthlyButton.setOnClickListener(this)
        actualNetValue.setOnClickListener(this)
        dashboardToolbar.setOnMenuItemClickListener(this)
        assets_accounts.text = getAccountsString(0)
        liabilities_accounts.text = getAccountsString(0)
    }

    private fun getAccountsString(quantity: Int) = resources.getQuantityString(R.plurals.accounts_number_par, quantity, quantity)

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.reportMonthlyButton -> navController.navigate(R.id.action_dashboardFragment_to_reportFragment)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.action_global_settingsFragment)
                true
            }
            else -> false
        }
    }
}
