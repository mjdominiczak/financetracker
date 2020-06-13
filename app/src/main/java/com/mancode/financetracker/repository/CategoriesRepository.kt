package com.mancode.financetracker.repository

import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.dao.CategoryDao
import com.mancode.financetracker.database.entity.CategoryEntity

class CategoriesRepository private constructor(categoryDao: CategoryDao) {

    val allCategories: LiveData<List<CategoryEntity>> = categoryDao.allCategories

    companion object {
        @Volatile
        private var instance: CategoriesRepository? = null

        fun getInstance(categoryDao: CategoryDao): CategoriesRepository =
                instance ?: synchronized(this) {
                    instance ?: CategoriesRepository(categoryDao).also { instance = it }
                }
    }

}