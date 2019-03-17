package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 03.02.2018.
 */

class BalanceExtended(@field:ColumnInfo(name = "_id")
                      var id: Int, @field:ColumnInfo(name = "balance_check_date")
                      @field:TypeConverters(DateConverter::class)
                      var checkDate: LocalDate,
                      @field:ColumnInfo(name = "balance_value")
                      var value: Double, @field:ColumnInfo(name = "account_name")
                      var accountName: String,
                      @field:ColumnInfo(name = "account_type")
                      var accountType: Int, @field:ColumnInfo(name = "account_currency")
                      var accountCurrency: String)
