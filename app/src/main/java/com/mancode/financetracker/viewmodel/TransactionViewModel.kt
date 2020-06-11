package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.TransactionFull
import com.mancode.financetracker.repository.TransactionsRepository
import com.mancode.financetracker.utils.InjectorUtils

/**
 * Created by Manveru on 12.02.2018.
 */

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionsRepository = InjectorUtils.getTransactionsRepository(application)
    val allTransactions: LiveData<List<TransactionFull>> by lazy { repository.allTransactions }

    fun restoreTransaction(transaction: TransactionEntity?) {
        if (transaction != null) {
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity?) {
        if (transaction != null) {
            repository.deleteTransaction(transaction)
        }
    }

    fun toggleBookmark(transaction: TransactionEntity) {
        repository.toggleBookmark(transaction)
    }
}