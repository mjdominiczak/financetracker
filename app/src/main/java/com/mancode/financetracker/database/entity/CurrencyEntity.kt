package com.mancode.financetracker.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 23.01.2018.
 */

@Entity(tableName = CurrencyEntity.TABLE_NAME)
@TypeConverters(DateConverter::class)
data class CurrencyEntity (

    @PrimaryKey @ColumnInfo(name = "currency_symbol") val currencySymbol: String,
    @ColumnInfo(name = "currency_exchange_rate") var exchangeRate: Double,
    @ColumnInfo(name = "currency_rate_date") var rateDate: LocalDate?
) {

    companion object {
        const val TABLE_NAME = "currencies"
    }
}
