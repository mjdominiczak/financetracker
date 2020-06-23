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

    fun updateCategory(categoryEntity: CategoryEntity) {
        AsyncTask.execute {
            categoryDao.updateCategory(categoryEntity)
        }
    }

    fun deleteCategory(categoryEntity: CategoryEntity) {
        AsyncTask.execute {
            categoryDao.deleteCategory(categoryEntity)
        }
    }

    fun toggleHidden(categoryId: Int) {
        AsyncTask.execute {
            TODO()
        }
    }

    fun getDependencyCount(id: Int) : LiveData<Int> = categoryDao.getDependencyCount(id)

    companion object {
        @Volatile
        private var instance: CategoriesRepository? = null

        fun getInstance(categoryDao: CategoryDao): CategoriesRepository =
                instance ?: synchronized(this) {
                    instance ?: CategoriesRepository(categoryDao).also { instance = it }
                }
    }

}