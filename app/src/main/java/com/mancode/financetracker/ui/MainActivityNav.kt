package com.mancode.financetracker.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.mancode.financetracker.R
import com.mancode.financetracker.notifications.resetRemindersAndShowDecisionDialog
import com.mancode.financetracker.utils.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
import com.mancode.financetracker.utils.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
import com.mancode.financetracker.utils.exportJson
import com.mancode.financetracker.utils.importJson
import com.mancode.financetracker.workers.fetchExchangeRates
import kotlinx.android.synthetic.main.activity_main_nav.*

class MainActivityNav : AppCompatActivity() {

    internal lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resetRemindersAndShowDecisionDialog()
        fetchExchangeRates()

        setContentView(R.layout.activity_main_nav)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        navController = host.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment -> hideBottomNav()
                R.id.reportFragment -> hideBottomNav()
                R.id.addEditAccountFragment -> hideBottomNav()
                R.id.addBalanceFragment -> hideBottomNav()
                R.id.addEditTransactionFragment -> hideBottomNav()
                R.id.categoryFragment -> hideBottomNav()
                else -> showBottomNav()
            }
        }
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportJson()
            }
            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                importJson()
            }
            else -> {
            }
        }
    }

    private fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
    }
}