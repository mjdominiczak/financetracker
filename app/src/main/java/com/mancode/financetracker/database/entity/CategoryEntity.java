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
    private int id;

    @ColumnInfo(name = "category")
    private String category;

    public CategoryEntity(int id, String category) {
        this.id = id;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return category;
    }
}
