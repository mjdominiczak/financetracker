package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.utils.InjectorUtils

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    val actualNetValue: LiveData<Double> = InjectorUtils.getNetValuesRepository(application)
            .actualNetValue
    val assetsAccountsNumber: LiveData<Int> = InjectorUtils.getAccountsRepository(application)
            .getAssetsAccountsCount()
    val liabilitiesAccountsNumber: LiveData<Int> = InjectorUtils.getAccountsRepository(application)
            .getLiabilitiesAccountsCount()
}