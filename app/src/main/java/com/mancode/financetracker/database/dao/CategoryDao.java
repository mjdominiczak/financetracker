package com.mancode.financetracker.database.dao;

import com.mancode.financetracker.database.entity.CategoryEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories")
    LiveData<List<CategoryEntity>> getAllCategoriesLive();

    @Query("SELECT * FROM categories")
    List<CategoryEntity> getAllCategories();

    @Query("SELECT * FROM categories WHERE category_type = 1")
    List<CategoryEntity> getIncomeCategoriesLive();

    @Query("SELECT * FROM categories WHERE category_type = -1")
    List<CategoryEntity> getOutcomeCategoriesLive();

    @Insert
    void insertCategory(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categoryList);
}
