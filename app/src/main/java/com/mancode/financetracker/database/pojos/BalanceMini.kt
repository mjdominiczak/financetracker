package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

class BalanceMini(
        @ColumnInfo(name = "balance_check_date")
        @TypeConverters(DateConverter::class) val checkDate: LocalDate,
        @ColumnInfo(name = "balance_value") val value: Double,
        @ColumnInfo(name = "account_type") val accountType: Int,
        @ColumnInfo(name = "account_currency") val accountCurrency: String
)