package com.mancode.financetracker.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.TransactionDao
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.TransactionFull
import org.threeten.bp.LocalDate

class TransactionsRepository private constructor(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<TransactionFull>> by lazy { transactionDao.allTransactions }

    fun getTransactionsForRange(from: LocalDate, to: LocalDate): LiveData<List<TransactionEntity>> =
            transactionDao.getTransactionsFromRange(from, to)

    fun getTransaction(id: Int): LiveData<TransactionEntity> = transactionDao.getTransaction(id)

    fun insertTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.insertTransaction(transaction) }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.updateTransaction(transaction) }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.deleteTransaction(transaction) }
    }

    fun toggleBookmark(transaction: TransactionEntity) {
        AsyncTask.execute { transactionDao.toggleFlag(transaction.id, TransactionEntity.BOOKMARKED) }
    }

    companion object {
        @Volatile
        private var instance: TransactionsRepository? = null

        fun getInstance(transactionDao: TransactionDao): TransactionsRepository =
                instance ?: synchronized(this) {
                    instance ?: TransactionsRepository(transactionDao).also { instance = it }
                }
    }
}