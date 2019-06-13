package com.mancode.financetracker.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.AccountMini
import com.mancode.financetracker.repository.DataRepository

/**
 * Created by Manveru on 12.02.2018.
 */

class AddEditTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = DataRepository.getInstance(application)
    val accountsNamesAndIds: List<AccountMini> by lazy { repository.accountsNamesAndIds }
    val categories: List<CategoryEntity> by lazy { repository.allCategories }

    fun insertTransaction(transaction: TransactionEntity) {
        repository.insertTransaction(transaction)
    }

    fun updateTransaction(transaction: TransactionEntity) {
        repository.updateTransaction(transaction)
    }
}
