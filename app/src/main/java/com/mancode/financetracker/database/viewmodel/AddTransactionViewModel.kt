package com.mancode.financetracker.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mancode.financetracker.database.DataRepository
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.AccountMini

/**
 * Created by Manveru on 12.02.2018.
 */

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = DataRepository.getInstance(application)
    val accountsNamesAndIds: List<AccountMini> by lazy { repository.accountsNamesAndIds }
    val incomeCategories: List<CategoryEntity> by lazy { repository.incomeCategories }
    val outcomeCategories: List<CategoryEntity> by lazy { repository.outcomeCategories }

    fun insertTransaction(transaction: TransactionEntity) {
        repository.insertTransaction(transaction)
    }

}
