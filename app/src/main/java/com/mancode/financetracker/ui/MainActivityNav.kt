package com.mancode.financetracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mancode.financetracker.R
import com.mancode.financetracker.notifications.resetRemindersAndShowDecisionDialog
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.workers.TAG_UPDATE
import com.mancode.financetracker.workers.fetchExchangeRates
import com.mancode.financetracker.workers.runExportToUri
import com.mancode.financetracker.workers.runImportFromUri
import kotlinx.android.synthetic.main.activity_main_nav.*

class MainActivityNav : AppCompatActivity() {

    internal lateinit var navController: NavController

    /** Nullability checks necessary for app to not crash on going back from selection activity */
    private val createDocument = registerForActivityResult(CreateJson()) {
        if (it != null) {
            applicationContext.runExportToUri(it)
        }
    }
    private val importDocument = registerForActivityResult(OpenDocument()) {
        if (it != null) {
            applicationContext.runImportFromUri(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PreferenceAccessor.firstRun) {
            resetRemindersAndShowDecisionDialog()
        }
        fetchExchangeRates()

        setContentView(R.layout.activity_main_nav)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        navController = host.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.firstRunFragment -> hideBottomNav()
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
        WorkManager.getInstance(applicationContext)
                .getWorkInfosByTagLiveData(TAG_UPDATE)
                .observe(this, { workinfos ->
                    if (workinfos.any { it.state == WorkInfo.State.RUNNING }) {
                        workerProgressIndicator.show()
                    } else {
                        workerProgressIndicator.hide()
                    }
                })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }

    private fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE
//        bottomNavigationView.animate()
//                .translationY(0f)
//                .setListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator?) {
//                        bottomNavigationView.visibility = View.VISIBLE
//                    }
//                })
    }

    private fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
//        bottomNavigationView.animate()
//                .translationY(bottomNavigationView.height.toFloat())
//                .setListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator?) {
//                        bottomNavigationView.visibility = View.GONE
//                    }
//                })
    }

    fun exportJson() {
        createDocument.launch(JSON_FILENAME)
    }

    fun importJson() {
        importDocument.launch(arrayOf("*/*", "application/json"))
    }

    private class CreateJson : CreateDocument() {
        override fun createIntent(context: Context, input: String): Intent {
            return super.createIntent(context, input)
                    .setType("application/json")
        }
    }
}

const val JSON_FILENAME = "ft_dump.json"