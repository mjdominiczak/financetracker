package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.AccountEntity
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.utils.InjectorUtils
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 12.02.2018.
 */

class AddEditTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val accountsRepo = InjectorUtils.getAccountsRepository(application)
    private val categoriesRepo = InjectorUtils.getCategoriesRepository(application)
    private val transactionsRepo = InjectorUtils.getTransactionsRepository(application)

    val accountsLiveData: LiveData<List<AccountEntity>> by lazy { accountsRepo.allAccounts }
    val categoriesLiveData: LiveData<List<CategoryEntity>> by lazy { categoriesRepo.allCategories }
    private val accounts get() = accountsLiveData.value ?: emptyList()
    private val allCategories get() = categoriesLiveData.value ?: emptyList()
    val incomeCategories by lazy { ArrayList<CategoryEntity>() }
    val outcomeCategories by lazy { ArrayList<CategoryEntity>() }
    var transaction: TransactionEntity? = null
    var category: CategoryEntity? = null
    var account: AccountEntity? = null

    fun getAccountsOnDate(date: LocalDate): List<AccountEntity> =
            accounts.filter {
                !it.openDate.isAfter(date) && (it.closeDate == null || it.closeDate!!.isAfter(date))
            }

    fun isAccountOpenOn(date: LocalDate): Boolean =
            account != null && !account!!.openDate.isAfter(date)

    fun isAccountClosedBefore(date: LocalDate): Boolean =
            account != null && (account!!.closeDate != null && account!!.closeDate!!.isBefore(date))

    fun getTransaction(id: Int): LiveData<TransactionEntity> = transactionsRepo.getTransaction(id)

    fun insertTransaction(transaction: TransactionEntity) {
        transactionsRepo.insertTransaction(transaction)
    }

    fun updateTransaction(transaction: TransactionEntity) {
        transactionsRepo.updateTransaction(transaction)
    }

    fun splitCategories() {
        incomeCategories.addAll(allCategories.filter { it.categoryType == TransactionEntity.TYPE_INCOME })
        outcomeCategories.addAll(allCategories.filter { it.categoryType == TransactionEntity.TYPE_OUTCOME })
    }

    fun getAccountName(): String? =
            when {
                account != null -> account!!.accountName
                transaction != null -> {
                    val accountIndex = accounts.map { it.id }.indexOf(transaction!!.accountId)
                    if (accountIndex >= 0) accounts[accountIndex].accountName else null
                }
                else -> null
            }

    fun getCategoryName(): String? =
            when {
                category != null -> category!!.category
                transaction != null -> {
                    val categoryIndex = allCategories.map { it.id }.indexOf(transaction?.categoryId)
                    if (categoryIndex >= 0) allCategories[categoryIndex].category else null
                }
                else -> null
            }

    fun getAccountIdByName(name: String) = accounts[accounts.map { it.accountName }.indexOf(name)].id
    fun getAccountIndex(name: String) = accounts.map { it.accountName }.indexOf(name)
    fun getIncomeCategoryIndex(name: String) = incomeCategories.map { it.category }.indexOf(name)
    fun getOutcomeCategoryIndex(name: String) = outcomeCategories.map { it.category }.indexOf(name)
}
