package com.mancode.financetracker.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Query("SELECT COUNT(*) FROM transactions WHERE transaction_category = :id")
    LiveData<Integer> getDependencyCount(int id);

    @Insert
    void insertCategory(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categoryList);

    @Update
    void updateCategory(CategoryEntity category);

    @Delete
    void deleteCategory(CategoryEntity categoryEntity);

    @Query("UPDATE categories " +
            "SET hidden = ~(hidden & 1) & (hidden | 1) " +
            "WHERE _id = :id")
    void toggleHidden(int id);
}
