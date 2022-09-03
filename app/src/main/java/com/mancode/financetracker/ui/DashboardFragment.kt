package com.mancode.financetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.mancode.financetracker.R
import com.mancode.financetracker.databinding.FragmentDashboardBinding
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.viewmodel.DashboardViewModel
import com.mancode.financetracker.workers.runUpdateWorker

class DashboardFragment : Fragment(R.layout.fragment_dashboard), View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        navController = findNavController()
        if (PreferenceAccessor.firstRun) navController.navigate(R.id.firstRunFragment)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.assetsAccountsNumber.observe(viewLifecycleOwner) {
            binding.assetsAccounts.text = getAccountsString(it)
        }
        viewModel.liabilitiesAccountsNumber.observe(viewLifecycleOwner) {
            binding.liabilitiesAccounts.text = getAccountsString(it)
        }
        viewModel.actualNetValue.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.actualNetValue.setFormattedMoney(it)
            } else {
                binding.actualNetValue.text = getString(R.string.not_available)
            }
        }
        viewModel.getActualAssets().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.actualAssets.setFormattedMoney(it)
            } else {
                binding.actualAssets.text = getString(R.string.not_available)
            }
        }
        viewModel.getActualLiabilities().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.actualLiabilities.setFormattedMoney(it)
            } else {
                binding.actualLiabilities.text = getString(R.string.not_available)
            }
        }
        with(binding) {
            reportMonthlyButton.setOnClickListener(this@DashboardFragment)
            dashboardToolbar.setOnMenuItemClickListener(this@DashboardFragment)
            assetsAccounts.text = getAccountsString(0)
            liabilitiesAccounts.text = getAccountsString(0)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
            R.id.action_force_update -> {
                requireContext().runUpdateWorker(force = true)
                true
            }
            else -> false
        }
    }
}
