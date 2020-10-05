package com.mancode.financetracker.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mancode.financetracker.R
import com.mancode.financetracker.notifications.resetRemindersAndShowDecisionDialog
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import com.mancode.financetracker.workers.ExportToJsonWorker
import com.mancode.financetracker.workers.ImportFromJsonWorker
import com.mancode.financetracker.workers.UpdateStateWorker
import com.mancode.financetracker.workers.fetchExchangeRates
import kotlinx.android.synthetic.main.activity_main_nav.*

class MainActivityNav : AppCompatActivity() {

    internal lateinit var navController: NavController

    /** Nullability checks necessary for app to not crash on going back from selection activity */
    private val createDocument = registerForActivityResult(CreateJson()) {
        if (it != null) {
            createJsonAtUri(it)
        }
    }
    private val importDocument = registerForActivityResult(OpenDocument()) {
        if (it != null) {
            importJsonFromUri(it)
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
    }

    private fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
    }

    private fun createJsonAtUri(uri: Uri) {
        val data = Data.Builder().apply {
            putString(ExportToJsonWorker.KEY_URI_ARG, uri.toString())
        }.build()
        val exportRequest = OneTimeWorkRequest.Builder(ExportToJsonWorker::class.java)
                .setInputData(data).build()
        WorkManager.getInstance(this).enqueue(exportRequest)
    }

    private fun importJsonFromUri(uri: Uri) {
        val data = Data.Builder().apply {
            putString(ImportFromJsonWorker.KEY_URI_ARG, uri.toString())
        }.build()
        val importRequest = OneTimeWorkRequest.Builder(ImportFromJsonWorker::class.java)
                .setInputData(data).build()
        val updateRequest = OneTimeWorkRequest.Builder(UpdateStateWorker::class.java).build()
        WorkManager.getInstance(this)
                .beginWith(importRequest)
                .then(updateRequest)
                .enqueue()
    }

    fun exportJson() {
        createDocument.launch(JSON_FILENAME)
    }

    fun importJson() {
        importDocument.launch(arrayOf("application/json"))
    }

    private class CreateJson : CreateDocument() {
        override fun createIntent(context: Context, input: String): Intent {
            return super.createIntent(context, input)
                    .setType("application/json")
        }
    }
}

const val JSON_FILENAME = "ft_dump.json"