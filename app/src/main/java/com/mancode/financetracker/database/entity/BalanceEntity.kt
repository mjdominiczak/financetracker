package com.mancode.financetracker.database.entity

import androidx.room.*
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 25.01.2018.
 */

@Entity(tableName = "balances",
        foreignKeys = [ForeignKey(entity = AccountEntity::class,
                parentColumns = ["_id"],
                childColumns = ["balance_account_id"])])
@TypeConverters(DateConverter::class)
data class BalanceEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id: Int,
        @ColumnInfo(name = "balance_check_date")
        val checkDate: LocalDate,
        @ColumnInfo(name = "balance_account_id")
        val accountId: Int,
        @ColumnInfo(name = "balance_value")
        var value: Double,
        @ColumnInfo(name = "balance_fixed")
        val fixed: Boolean)
