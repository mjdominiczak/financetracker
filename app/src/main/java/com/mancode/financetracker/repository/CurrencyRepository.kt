package com.mancode.financetracker.repository

import android.os.AsyncTask
import com.mancode.financetracker.database.dao.CurrencyDao
import com.mancode.financetracker.database.entity.CurrencyEntity

class CurrencyRepository private constructor(
        private val currencyDao: CurrencyDao) {

    fun insertAll(currencies: List<CurrencyEntity>) {
        AsyncTask.execute { currencyDao.insertAll(currencies) }
    }

    companion object {
        @Volatile
        private var instance: CurrencyRepository? = null

        fun getInstance(currencyDao: CurrencyDao): CurrencyRepository =
                instance ?: synchronized(this) {
                    instance ?: CurrencyRepository(currencyDao).also { instance = it }
                }
    }
}