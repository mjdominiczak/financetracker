package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

class BalanceMini {

    @ColumnInfo(name = "balance_check_date")
    @TypeConverters(DateConverter::class)
    var checkDate: LocalDate? = null

    @ColumnInfo(name = "balance_value")
    var value: Double = 0.toDouble()

    @ColumnInfo(name = "account_type")
    var accountType: Int = 0

    @ColumnInfo(name = "account_currency")
    var AccountCurrency: String? = null
}
