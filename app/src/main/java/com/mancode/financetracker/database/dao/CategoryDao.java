package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mancode.financetracker.database.entity.CategoryEntity;

import java.util.List;

/**
 * Created by Manveru on 25.01.2018.
 */

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories")
    LiveData<List<CategoryEntity>> getAllCategoriesLive();

    @Query("SELECT * FROM categories")
    List<CategoryEntity> getAllCategories();

    @Insert
    void insertCategory(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categoryList);
}
