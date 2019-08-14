package com.mancode.financetracker.database.entity

import androidx.room.*
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 23.01.2018.
 */

@Entity(tableName = "accounts",
        foreignKeys = [ForeignKey(entity = CurrencyEntity::class,
                parentColumns = ["currency_symbol"],
                childColumns = ["account_currency"])])
@TypeConverters(DateConverter::class)
data class AccountEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id: Int,
        @ColumnInfo(name = "account_name")
        var accountName: String,
        @ColumnInfo(name = "account_type")
        var accountType: Int,
        @ColumnInfo(name = "account_currency")
        var currency: String,
        @ColumnInfo(name = "account_open_date")
        var openDate: LocalDate,
        @ColumnInfo(name = "account_close_date")
        var closeDate: LocalDate?) {

    override fun toString(): String {
        return accountName
    }
}