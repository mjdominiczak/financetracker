package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.repository.NetValuesRepository

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val netValuesRepository = NetValuesRepository.getInstance(
            FTDatabase.getInstance(application).netValueDao())

    val netValues: LiveData<List<NetValue>> = netValuesRepository.netValues
    val keyedNetValues: LiveData<List<NetValue>> = netValuesRepository.keyedNetValues
}