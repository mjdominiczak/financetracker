package com.mancode.financetracker.database.pojos

import androidx.room.ColumnInfo

data class AccountMini(
    @ColumnInfo(name = "_id") val id: Int,
    @ColumnInfo(name = "account_name") val accountName: String
) {
    override fun toString(): String {
        return accountName
    }
}