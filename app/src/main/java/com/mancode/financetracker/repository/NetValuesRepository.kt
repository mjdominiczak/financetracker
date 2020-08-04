package com.mancode.financetracker.repository

import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.NetValueDao
import com.mancode.financetracker.database.entity.NetValue
import org.threeten.bp.LocalDate

class NetValuesRepository private constructor(private val netValueDao: NetValueDao) {

    val actualNetValue: LiveData<Double> = netValueDao.actualNetValue
    val netValues: LiveData<List<NetValue>> = netValueDao.values
    val keyedNetValues: LiveData<List<NetValue>> = netValueDao.keyedValues

    fun getNetValueClosestTo(date: LocalDate): LiveData<NetValue> = netValueDao.getValueClosestTo(date)

    companion object {
        @Volatile
        private var instance: NetValuesRepository? = null

        fun getInstance(netValueDao: NetValueDao): NetValuesRepository =
                instance ?: synchronized(this) {
                    instance ?: NetValuesRepository(netValueDao).also { instance = it }
                }
    }
}