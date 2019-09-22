package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.TransactionFull
import com.mancode.financetracker.repository.DataRepository

/**
 * Created by Manveru on 12.02.2018.
 */

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = DataRepository.getInstance(application)
    val allTransactions: LiveData<List<TransactionFull>> by lazy { repository.allTransactions }

    fun deleteTransaction(transaction: TransactionEntity) {
        repository.deleteTransaction(transaction)
    }

    fun toggleBookmark(transaction: TransactionEntity) {
        repository.toggleBookmark(transaction)
    }
}