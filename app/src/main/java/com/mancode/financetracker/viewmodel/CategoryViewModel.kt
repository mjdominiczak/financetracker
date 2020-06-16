package com.mancode.financetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.utils.InjectorUtils

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InjectorUtils.getCategoriesRepository(application)

    val outcomeCategories: LiveData<List<CategoryEntity>> = repository.outcomeCategories
    val incomeCategories: LiveData<List<CategoryEntity>> = repository.incomeCategories
}
