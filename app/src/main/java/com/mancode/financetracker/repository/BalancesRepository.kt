package com.mancode.financetracker.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.BalanceDao
import com.mancode.financetracker.database.dao.NetValueDao
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.pojos.BalanceExtended

class BalancesRepository private constructor(
        private val netValueDao: NetValueDao,
        private val balanceDao: BalanceDao) {

    fun getKeyedNetValues(): LiveData<List<NetValue>> = netValueDao.keyedValues

    fun getBalances(): LiveData<List<BalanceExtended>> = balanceDao.balancesForDisplay

    fun insertBalance(balance: BalanceEntity) {
        AsyncTask.execute { balanceDao.insertBalance(balance) }
    }

    fun removeLastBalance() {
        AsyncTask.execute { balanceDao.removeLast() }
    }

    companion object {
        @Volatile
        private var instance: BalancesRepository? = null

        fun getInstance(netValueDao: NetValueDao, balanceDao: BalanceDao): BalancesRepository =
                instance ?: synchronized(this) {
                    instance ?: BalancesRepository(netValueDao, balanceDao).also { instance = it }
                }
    }

}