package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.database.entity.TransactionEntity
import org.threeten.bp.LocalDate

@TypeConverters(DateConverter::class)
class TransactionFull(
        @Embedded val transaction: TransactionEntity,
        @ColumnInfo(name = "account_name") val accountName: String,
        @ColumnInfo(name = "account_type") val accountType: Int,
        @ColumnInfo(name = "account_currency") val currency: String,
        @ColumnInfo(name = "account_open_date") val openDate: LocalDate,
        @ColumnInfo(name = "account_close_date") val closeDate: LocalDate?,
        @ColumnInfo(name = "category") val category: String,
        @ColumnInfo(name = "category_type") val categoryType: Int
)