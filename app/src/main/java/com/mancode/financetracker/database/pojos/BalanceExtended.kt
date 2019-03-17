package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

@TypeConverters(DateConverter::class)
class BalanceExtended(
        @ColumnInfo(name = "_id") val id: Int,
        @ColumnInfo(name = "balance_check_date") val checkDate: LocalDate,
        @ColumnInfo(name = "balance_value") val value: Double,
        @ColumnInfo(name = "account_name") val accountName: String,
        @ColumnInfo(name = "account_type") val accountType: Int,
        @ColumnInfo(name = "account_currency") val accountCurrency: String
)
