package com.mancode.financetracker.database.entity

import androidx.room.*
import com.mancode.financetracker.database.converter.DateConverter
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 25.01.2018.
 */

@Entity(tableName = "transactions",
        foreignKeys = [
            ForeignKey(entity = AccountEntity::class,
                    parentColumns = ["_id"],
                    childColumns = ["transaction_account"]),
            ForeignKey(entity = CategoryEntity::class,
                    parentColumns = ["_id"],
                    childColumns = ["transaction_category"])])
@TypeConverters(DateConverter::class)
data class TransactionEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id: Int,
        @ColumnInfo(name = "transaction_date")
        var date: LocalDate,
        @ColumnInfo(name = "transaction_type")
        var type: Int,
        @ColumnInfo(name = "transaction_description")
        var description: String,
        @ColumnInfo(name = "transaction_value")
        var value: Double,
        @ColumnInfo(name = "transaction_flags", defaultValue = "0")
        var flags: Int,
        @ColumnInfo(name = "transaction_account")
        var accountId: Int,
        @ColumnInfo(name = "transaction_category")
        var categoryId: Int) {

    companion object {
        const val TYPE_INCOME = 1
        const val TYPE_OUTCOME = -1

        // FLAGS
        const val BOOKMARKED = 1
    }
}
