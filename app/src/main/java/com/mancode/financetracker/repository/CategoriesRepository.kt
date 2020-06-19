package com.mancode.financetracker.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.CategoryDao
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity

class CategoriesRepository private constructor(private val categoryDao: CategoryDao) {

    val allCategories: LiveData<List<CategoryEntity>> = categoryDao.allCategories
    val incomeCategories: LiveData<List<CategoryEntity>> =
            categoryDao.getCategoriesOfType(TransactionEntity.TYPE_INCOME)
    val outcomeCategories: LiveData<List<CategoryEntity>> =
            categoryDao.getCategoriesOfType(TransactionEntity.TYPE_OUTCOME)

    fun insertCategory(category: CategoryEntity) {
        AsyncTask.execute {
            categoryDao.insertCategory(category)
        }
    }

    companion object {
        @Volatile
        private var instance: CategoriesRepository? = null

        fun getInstance(categoryDao: CategoryDao): CategoriesRepository =
                instance ?: synchronized(this) {
                    instance ?: CategoriesRepository(categoryDao).also { instance = it }
                }
    }

}