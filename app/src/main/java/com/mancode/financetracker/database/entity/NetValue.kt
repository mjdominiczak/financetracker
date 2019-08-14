package com.mancode.financetracker.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

@Entity(tableName = "net_values")
@TypeConverters(DateConverter::class)
data class NetValue(
        @PrimaryKey
        val date: LocalDate,
        var value: Double,
        val calculated: Boolean) {

    var complete: Boolean = false

}