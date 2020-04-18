package com.mancode.financetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.mancode.financetracker.R
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {

    lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        netValueChart.setOnClickListener(this)
        actualNetValue.setOnClickListener(this)
        dashboardToolbar.setOnMenuItemClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.netValueChart -> navController.navigate(R.id.action_dashboardFragment_to_reportFragment)
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
