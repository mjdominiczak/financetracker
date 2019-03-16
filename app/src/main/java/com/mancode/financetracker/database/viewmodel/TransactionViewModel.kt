package com.mancode.financetracker.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.DataRepository
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.entity.TransactionFull

/**
 * Created by Manveru on 12.02.2018.
 */

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: DataRepository
    val allTransactions: LiveData<List<TransactionFull>>

    init {
        mRepository = DataRepository.getInstance(application)
        allTransactions = mRepository.allTransactions
    }

    fun insertTransaction(transaction: TransactionEntity) {
        mRepository.insertTransaction(transaction)
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        mRepository.deleteTransaction(transaction)
    }
}
