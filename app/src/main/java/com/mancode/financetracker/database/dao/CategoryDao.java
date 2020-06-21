package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mancode.financetracker.database.entity.CategoryEntity;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories")
    LiveData<List<CategoryEntity>> getAllCategories();

    @Query("SELECT * FROM categories")
    List<CategoryEntity> getAllCategoriesSimple();

    @Query("SELECT * FROM categories WHERE category_type = :type")
    LiveData<List<CategoryEntity>> getCategoriesOfType(int type);

    @Insert
    void insertCategory(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categoryList);

    @Update
    void updateCategory(CategoryEntity category);
}
