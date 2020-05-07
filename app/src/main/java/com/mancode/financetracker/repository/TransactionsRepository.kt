package com.mancode.financetracker.repository

import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.TransactionDao
import com.mancode.financetracker.database.entity.TransactionEntity
import org.threeten.bp.LocalDate

class TransactionsRepository private constructor(private val transactionDao: TransactionDao) {

    fun getTransactionsForRange(from: LocalDate, to: LocalDate): LiveData<List<TransactionEntity>> =
            transactionDao.getTransactionsFromRange(from, to)

    companion object {
        @Volatile
        private var instance: TransactionsRepository? = null

        fun getInstance(transactionDao: TransactionDao): TransactionsRepository =
                instance ?: synchronized(this) {
                    instance ?: TransactionsRepository(transactionDao).also { instance = it }
                }
    }
}