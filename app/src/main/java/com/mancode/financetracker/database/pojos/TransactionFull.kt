package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.database.entity.TransactionEntity
import org.threeten.bp.LocalDate

@TypeConverters(DateConverter::class)
class TransactionFull(@field:Embedded
                      var transaction: TransactionEntity,
                      @field:ColumnInfo(name = "account_name")
                      var accountName: String,
                      @field:ColumnInfo(name = "account_type")
                      var accountType: Int,
                      @field:ColumnInfo(name = "account_currency")
                      var currency: String,
                      @field:ColumnInfo(name = "account_open_date")
                      var openDate: LocalDate,
                      @field:ColumnInfo(name = "account_close_date")
                      var closeDate: LocalDate,
                      @field:ColumnInfo(name = "category")
                      var category: String,
                      @field:ColumnInfo(name = "category_type")
                      var categoryType: Int)
