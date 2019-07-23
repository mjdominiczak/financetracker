package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo

data class AccountNameCurrency(
    @ColumnInfo(name = "_id") val id: Int,
    @ColumnInfo(name = "account_name") val accountName: String,
    @ColumnInfo(name = "account_currency") val currency: String
)