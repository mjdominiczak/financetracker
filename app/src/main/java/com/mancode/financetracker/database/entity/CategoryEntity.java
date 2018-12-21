package com.mancode.financetracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Manveru on 25.01.2018.
 */

@Entity(tableName = "categories")
public class CategoryEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "category_type")
    public int categoryType;

    public CategoryEntity(int id, String category, int categoryType) {
        this.id = id;
        this.category = category;
        this.categoryType = categoryType;
    }

    @Override
    public String toString() {
        return category;
    }
}
