package com.mancode.financetracker.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.BalanceDao
import com.mancode.financetracker.database.dao.NetValueDao
import com.mancode.financetracker.database.entity.BalanceEntity
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.database.pojos.BalanceExtended
import org.threeten.bp.LocalDate

class BalancesRepository private constructor(
        private val netValueDao: NetValueDao,
        private val balanceDao: BalanceDao) {

    fun getKeyedNetValues(): LiveData<List<NetValue>> = netValueDao.keyedValues

    fun getBalancesForDate(date: LocalDate): LiveData<List<BalanceExtended>> = balanceDao.getFullBalancesForDate(date)

    fun getActualAssets(): LiveData<List<BalanceEntity>> = balanceDao.actualAssets
    fun getActualLiabilities(): LiveData<List<BalanceEntity>> = balanceDao.actualLiabilities

    fun insertBalance(balance: BalanceEntity) {
        AsyncTask.execute { balanceDao.insertBalance(balance) }
    }

    fun updateBalance(balance: BalanceEntity) {
        AsyncTask.execute { balanceDao.updateBalance(balance) }
    }

    fun removeBalance(balance: BalanceEntity) {
        AsyncTask.execute { balanceDao.removeBalance(balance) }
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