package com.mancode.financetracker.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mancode.financetracker.R
import com.mancode.financetracker.notifications.resetRemindersAndShowDecisionDialog
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.utils.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
import com.mancode.financetracker.utils.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
import com.mancode.financetracker.utils.exportJson
import com.mancode.financetracker.utils.importJson
import com.mancode.financetracker.workers.FetchECBRatesWorker
import com.mancode.financetracker.workers.fetchExchangeRates
import kotlinx.android.synthetic.main.activity_main_nav.*
import org.threeten.bp.LocalDate

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
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
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
}