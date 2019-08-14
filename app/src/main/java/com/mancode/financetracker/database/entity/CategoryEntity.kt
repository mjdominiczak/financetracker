package com.mancode.financetracker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Manveru on 25.01.2018.
 */

@Entity(tableName = "categories")
data class CategoryEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id: Int,
        @ColumnInfo(name = "category")
        var category: String,
        @ColumnInfo(name = "category_type")
        var categoryType: Int) {

    override fun toString(): String {
        return category
    }
}
